package service;

import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class AccountStore {

    // HashMap keyed by account number
    private final HashMap<String, Account> accounts = new HashMap<>();

    // Transaction history log
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
        transactionHistory.add("ADDED account "
                + account.getAccountNumber()
                + " | owner: " + account.getOwnerName()
                + " | balance: Rs. " + account.getBalance());
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    // Returns accounts sorted by balance (highest first) using a Comparator
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();
        sorted.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return sorted;
    }

    public void logTransaction(String entry) {
        transactionHistory.add(entry);
    }

    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public int size() {
        return accounts.size();
    }
}
