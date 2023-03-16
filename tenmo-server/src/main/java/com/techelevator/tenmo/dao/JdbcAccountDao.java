package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet balanceResult = jdbcTemplate.queryForRowSet(sql, userId);

        BigDecimal balance = null;
        while (balanceResult.next()) {
            Account account = mapRowToAccount(balanceResult);
            balance = account.getBalance();
        }
        return balance;
    }

    // Kate: added a method for getting all the information about the account for the following userId
    @Override
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet balanceResult = jdbcTemplate.queryForRowSet(sql, userId);

        Account account = null;
        while (balanceResult.next()) {
            account = mapRowToAccount(balanceResult);
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet row) {
        Account account = new Account();
        account.setAccountId(row.getInt("account_id"));
        account.setBalance(row.getBigDecimal("balance"));
        account.setUserId(row.getInt("user_id"));
        return account;
    }

}
