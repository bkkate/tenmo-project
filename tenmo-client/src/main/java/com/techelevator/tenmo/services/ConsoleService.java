package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printApproveOrReject() {
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject. Exit");
        System.out.println("------------");
    }

    public void printUsers(List<User> users) {

        System.out.printf("%10s %10s", "User ID", "User Name");
        System.out.println();
        for (User user : users) {
            System.out.printf("%10s %10s\n", String.valueOf(user.getId()), user.getUsername());
        }
    }

    public void printTransfersByAccountId(List<Transfer> transfers, UserService userService, int currentUserAccountId) {

        System.out.printf("%20s\n", "Transfers");
        System.out.println("-----------------------------------");
        System.out.printf("%5s %12s %12s", "ID", "From/To", "Amount");
        System.out.println();
        System.out.println("-----------------------------------");

        for (Transfer transfer : transfers) {
            String senderName;
            // does current user accountId match accountToId? if so, print "From: "
            if (transfer.getAccountToId() == currentUserAccountId) {
                senderName = userService.getUsernameByAccountId(transfer.getAccountFromId());
                System.out.printf("%5s %15s %10s\n", transfer.getTransferId(), "From: " + senderName, "$" + transfer.getAmount());
            }
            else { // or else, print "To: "
                senderName = userService.getUsernameByAccountId(transfer.getAccountToId());
                System.out.printf("%5s %15s %10s\n", transfer.getTransferId(), "To: " + senderName, "$" + transfer.getAmount());
            }
        }
    }

    //TODO: print only pending transfers
    public void printPendingTransfers(List<Transfer> transfers, UserService userService, int currentUserAccountId) {

        System.out.printf("%20s\n", "Pending Transfers");
        System.out.println("-----------------------------------");
        System.out.printf("%5s %12s %12s", "ID", "To", "Amount");
        System.out.println();
        System.out.println("-----------------------------------");

        for (Transfer transfer : transfers) {
            String senderName = userService.getUsernameByAccountId(transfer.getAccountToId());
                System.out.printf("%5s %15s %10s\n", transfer.getTransferId(), senderName, "$" + transfer.getAmount());
        }
    }

    public void printTransferDetails(Transfer transfer, UserService userService) {
        // TODO: may factor out verifying transferType & transferStatus (86-102) as a separate method
        String transferType = null;
        String transferStatus = null;

        // evaluates transfer type and status for printing
        if (transfer.getTransferTypeId() == 1) {
            transferType = "Request";
        } else {
            transferType = "Send";
        }
        switch (transfer.getTransferStatusId()) {
            case 1: transferStatus = "Pending";
            break;
            case 2: transferStatus = "Approved";
            break;
            case 3: transferStatus = "Rejected";
            break;
        }

        System.out.println("\n-----------------------------------");
        System.out.println("         Transfer Details");
        System.out.println("-----------------------------------");
        System.out.println("ID: " + transfer.getTransferId());
        System.out.println("From: " + userService.getUsernameByAccountId(transfer.getAccountFromId()));
        System.out.println("To: " + userService.getUsernameByAccountId(transfer.getAccountToId()));
        System.out.println("Type: " + transferType);
        System.out.println("Status: " + transferStatus);
        System.out.println("Amount: " + transfer.getAmount());
        System.out.println("-----------------------------------");
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
