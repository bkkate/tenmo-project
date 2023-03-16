package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transfer {
    @JsonProperty("transfer_id")
    private int transferId;
    @JsonProperty("transfer_type_id")
    private int transferTypeId;
    @JsonProperty("transfer_status_id")
    private int transferStatusId;
    @JsonProperty("account_from")
    private int accountFromId;
    @JsonProperty("account_to")
    private int accountToId;
    @JsonProperty("amount")
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer (int accountFromId, int accountToId, BigDecimal amount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }
    public int getTransferTypeId() { return transferTypeId; }
    public int getTransferStatusId() {
        return transferStatusId;
    }
    public int getAccountFromId() {
        return accountFromId;
    }
    public int getAccountToId() {
        return accountToId;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
    public void setTransferTypeId(int transferTypeId) {this.transferTypeId = transferTypeId;}
    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }
    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }
    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() { return ""; }

    }


