package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    public BigDecimal getBalanceByUserId(int userId);
    public Account getAccountByUserId(int userId);
}
