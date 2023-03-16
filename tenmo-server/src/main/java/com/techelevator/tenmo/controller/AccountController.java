package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private final AccountDao accountDao;

    public AccountController(JdbcAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    // gets the account using the id in the path
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable int id) {
        return accountDao.getAccountByUserId(id);
    }
}


