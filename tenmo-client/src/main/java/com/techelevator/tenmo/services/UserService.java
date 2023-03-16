package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public List<User> getAllUsers() {
        ResponseEntity<User[]> users = restTemplate.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class);
        List<User> usersList = Arrays.asList(users.getBody());
        return usersList;
    }

    public String getUsernameByAccountId(int accountId) {
        User user = null;
        try {
            ResponseEntity<User> userResponse = restTemplate.exchange(API_BASE_URL +
                    "users/" + accountId, HttpMethod.GET, makeAuthEntity(), User.class);

            user = userResponse.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user.getUsername();
    }

    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(user, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
