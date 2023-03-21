package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    public Transfer getTransferByTransferId(int transferId);

    // modified the name for better use in step 7 (this method was previously not being used)
    public List<Transfer> getTransfersByAccountId(int accountId);

    public Transfer sendMoney(Transfer newTransfer);

    public Transfer requestMoney(Transfer newTransfer);

    public List<Transfer> getPendingTransfers(int accountId);

}
