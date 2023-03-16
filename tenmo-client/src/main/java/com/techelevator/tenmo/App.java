package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    // Account, Transfer, and User services will have token set up in handleLogin method
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();

    // these three will be initialized in handleLogin method as user logs in
    private AuthenticatedUser currentUser;
    private int userId;
    private Account accountWithCurrentUserId;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);

        if (currentUser != null) {
            String token = currentUser.getToken(); // gets token of current user

            accountService.setAuthToken(token);
            transferService.setAuthToken(token);
            userService.setAuthToken(token);

            userId =  currentUser.getUser().getId();
            accountWithCurrentUserId = accountService.getAccountByUserId(userId);

        } else {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                System.out.println("\n\tYour current balance is: $" + viewCurrentBalance());
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private BigDecimal viewCurrentBalance() {
        BigDecimal balance = null;

        if (accountWithCurrentUserId != null) {
            balance = accountService.getAccountByUserId(userId).getBalance();
//            System.out.println("Your current account balance is: $" + balance);
        } else {
            consoleService.printErrorMessage();
        }
        return balance;
	}

    // gets & prints list of all past transfers for current user
	private void viewTransferHistory() {  // TODO: enter 0 to exit
        List<Transfer> transfers = transferService.viewTransfersOfAccount(accountWithCurrentUserId.getAccountId());

        if (transfers.size() > 0) {
            consoleService.printTransfersByAccountId(transfers, userService, accountWithCurrentUserId.getAccountId());
            int response = consoleService.promptForInt("\nPlease enter a transfer ID number for details (Enter 0 to exit): ");

                if (response == 0) { return; }

                Transfer returnedTransfer = verifyTransfer(transfers, response);
                while (returnedTransfer == null) {
                    response = consoleService.promptForInt("Invalid. Please enter valid ID: ");
                    returnedTransfer = verifyTransfer(transfers, response);
                }
                consoleService.printTransferDetails(returnedTransfer, userService);
        } else {
            System.out.println("\n\tYou have not sent or received any transfers yet.");
        }
	}

    private Transfer verifyTransfer(List<Transfer> transfers, int response) {
        Transfer transferIfExists = null;
        for (Transfer transfer : transfers) {
            if (transfer.getTransferId() == response) {
                // call consoleService method to print the details of transfer by passing in a transfer object as an argument
                transferIfExists = transfer;
                break;
            }
        }
        return transferIfExists;

    }


	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

    //TODO give an exit option to return to main menu
	private void sendBucks() { // display list of users, prompt for their choice

        int userIdTo = displayUsernameAndReturnInput();
        BigDecimal amountToSend = verifySufficientFunds();

        int accountFromId = accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId();
        int accountToId = accountService.getAccountByUserId(userIdTo).getAccountId();

        Transfer transferInfo = new Transfer(accountFromId, accountToId, amountToSend); // create transfer object
        boolean successfulTransfer = transferService.sendMoney(transferInfo); // use as arg for sendMoney method
            if (successfulTransfer) {
                System.out.println("Transfer was successfully completed");
            }
            else {
                System.out.println("Was not able to complete transfer");
            }
	}

    private int displayUsernameAndReturnInput() {
        int userIdFrom = currentUser.getUser().getId();
        Account accountWithUserId = accountService.getAccountByUserId(userIdFrom);  // accountService: returns list of users
        List<User> users = userService.getAllUsers();
        consoleService.printUsers(users);   // consoleService: prints list of registered users

        boolean validInput = false;
        int userIdTo = userIdFrom;

        while (!validInput) {  // prompt for input, and check id response
            userIdTo = consoleService.promptForInt("Enter the recipient's user ID: ");
            if (userIdTo != userIdFrom && (userIdIsValid(users, userIdTo))) {   // lets user out of loop when valid choice is made
                validInput = true;
            } else {
                System.out.println("Please choose a User ID from the list that is not your own.");
            }
        }
        return userIdTo;
    }


    public BigDecimal verifySufficientFunds() {
        boolean validInput = false;
        BigDecimal amountToSend = new BigDecimal("0.00");

        while (!validInput) {   // prompt for transfer amount
            amountToSend = consoleService.promptForBigDecimal("Please enter the amount to send: ");

            if (amountToSend.compareTo(new BigDecimal("0.00")) == 1 && amountToSend.compareTo(viewCurrentBalance()) <= 0) {
                validInput = true;   // lets user out of loop when valid choice is made
            } else {
                System.out.print("\nPlease enter an amount greater than $0.00, " +
                        "up to your current balance. \n");
                System.out.println("\n\tYour current balance is: $" + viewCurrentBalance());
                System.out.println();
            }
        }
        return amountToSend;
    }


	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    private boolean userIdIsValid(List<User> users, int userIdTo) {
        boolean idIsValid = false;

        for (User user : users) {
            if (user.getId() == userIdTo) {
                idIsValid = true;
            }
        }

        return idIsValid;
    }

}
