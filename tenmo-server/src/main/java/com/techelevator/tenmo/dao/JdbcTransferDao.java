package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.jboss.logging.BasicLogger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final int TRANSFER_REQUEST = 1;
    private final int TRANSFER_SEND = 2;
    private final int TRANSFER_STATUS_PENDING = 1;
    private final int TRANSFER_STATUS_APPROVED = 2;
    private final int TRANSFER_STATUS_REJECTED = 3;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransferByTransferId(int transferId) {
        String sql = "SELECT * FROM transfer WHERE transfer_id= ?;";
        SqlRowSet transferResult = jdbcTemplate.queryForRowSet(sql, transferId);

        Transfer transfer = null;
        if (transferResult.next()) {
            transfer = mapRowToTransfer(transferResult);

        }
        return transfer;
    }

    // pulls a list of all transfers of a single account
    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {

        List<Transfer> transfersOfAccount = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to =?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

        while (results.next()) {
            transfersOfAccount.add(mapRowToTransfer(results));
        }
        return transfersOfAccount;
    }

    // pulls a list of pending transfers of a single account
    @Override
    public List<Transfer> getPendingTransfers(int accountId) {

        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from= ? AND transfer_status_id= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, TRANSFER_STATUS_PENDING);

        while (results.next()) {
            pendingTransfers.add(mapRowToTransfer(results));
        }
        return pendingTransfers;
    }

    public Transfer sendMoney(Transfer newTransfer) {

        Transfer updatedTransfer = null;
        if (accountHasEnoughBalance(newTransfer.getAccountFromId(), newTransfer.getAmount())) {
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";

            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, TRANSFER_SEND, TRANSFER_STATUS_APPROVED, newTransfer.getAccountFromId(),
                                newTransfer.getAccountToId(), newTransfer.getAmount());
            updatedTransfer = getTransferByTransferId(newTransferId);

            updateRecipientAccountBalance(updatedTransfer.getAccountToId(), updatedTransfer.getAmount());
            updateSenderAccountBalance(updatedTransfer.getAccountFromId(), updatedTransfer.getAmount());
        }
        return updatedTransfer;
    }

    // updates the transfer table with a new transfer request into and sets the transfer_status to PENDING
    @Override
    public Transfer requestMoney(Transfer newTransfer) {

        Transfer updatedTransfer;

        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";

        int newTransferId = 0;
        try {
          newTransferId = jdbcTemplate.queryForObject(sql, int.class, TRANSFER_REQUEST, TRANSFER_STATUS_PENDING,
                    newTransfer.getAccountFromId(), newTransfer.getAccountToId(), newTransfer.getAmount());
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
        }

        updatedTransfer = getTransferByTransferId(newTransferId);

        return updatedTransfer;
    }

    // a helper function for checking if there's enough balance
    public boolean accountHasEnoughBalance(int accountId, BigDecimal amount) {
        boolean isGreaterThanZero = false;
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        BigDecimal balanceReturned = new BigDecimal("0.00");

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId);
        if (result.next()) {
            balanceReturned = result.getBigDecimal("balance");
        }
        if (balanceReturned.compareTo(amount) >= 0) {
            isGreaterThanZero = true;
        }
        return isGreaterThanZero;
    }

    private void updateRecipientAccountBalance(int recipientAccountId, BigDecimal amountToSend) {

        BigDecimal originalRecipientBalance = getBalanceByAccountId(recipientAccountId);
        BigDecimal updatedRecipientBalance = originalRecipientBalance.add(amountToSend);

        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, updatedRecipientBalance, recipientAccountId);

    }

    private void updateSenderAccountBalance(int senderAccountId, BigDecimal amountToSend) {

        BigDecimal originalSenderBalance = getBalanceByAccountId(senderAccountId);
        BigDecimal updatedSenderBalance = originalSenderBalance.subtract(amountToSend);

        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, updatedSenderBalance, senderAccountId);
    }

    private BigDecimal getBalanceByAccountId(int accountId) {
        String sql = "SELECT balance FROM account WHERE account_id= ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId);

        BigDecimal accountBalance = null;
        if (result.next()) {
            accountBalance = result.getBigDecimal("balance");
        }
        return accountBalance;
    }

    private Transfer mapRowToTransfer (SqlRowSet row) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(row.getInt("transfer_id"));
        transfer.setTransferStatusId(row.getInt("transfer_status_id"));
        transfer.setAccountFromId(row.getInt("account_from"));
        transfer.setAccountToId(row.getInt("account_to"));
        transfer.setAmount(row.getBigDecimal("amount"));
        return transfer;
    }

}
