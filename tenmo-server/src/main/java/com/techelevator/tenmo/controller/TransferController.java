package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/transfer")
@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    private final TransferDao transferDao;

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public TransferController(JdbcTransferDao transferDao) { this.transferDao = transferDao; }

    @RequestMapping( method = RequestMethod.POST)
    public Transfer newTransfer(@RequestBody Transfer transfer) {
        return transferDao.sendMoney(transfer);
    }

    // handling request to view all transfers by an account ID
    @RequestMapping( path = "/{accountId}", method = RequestMethod.GET)
    public List<Transfer> viewTransfersOfAccount(@PathVariable int accountId) {
        return transferDao.getTransfersByAccountId(accountId);
    }

    // handles request to view pending transfers by an account ID
    @RequestMapping (path = "/{accountId}/pending")
    public List<Transfer> viewPendingTransfers(@PathVariable int accountId) {
        return transferDao.getPendingTransfers(accountId);
    }

    // handles request to make a money request from another user
    @RequestMapping(path="/request", method = RequestMethod.POST)
    public Transfer createTransferRequest(@RequestBody Transfer transfer) {
        return transferDao.requestMoney(transfer);
    }

}
