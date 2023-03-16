package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    //TODO: could maybe use a try-catch block to handle server/access error?
    public boolean sendMoney (Transfer transfer) {

        boolean success = false;

        Transfer returnedTransfer = restTemplate.postForObject(API_BASE_URL + "transfer",
                makeTransferEntity(transfer), Transfer.class);

        if (returnedTransfer != null) {
            success = true;
        }
        return success;
    }

    public List<Transfer> viewTransfersOfAccount (int accountId) {
        ResponseEntity<Transfer[]> transfers = restTemplate.exchange(API_BASE_URL + "transfer/" + accountId,
                                                    HttpMethod.GET, makeAuthEntity(), Transfer[].class);
        List<Transfer> transferList = Arrays.asList(transfers.getBody());
        return transferList;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}