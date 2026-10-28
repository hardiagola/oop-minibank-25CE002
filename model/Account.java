package model;

import java.util.Objects;

public abstract class Account
        implements Transactable, InterestBearing {

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public Account(String ownerName, long openingBalance) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public abstract boolean canWithdraw(long amount);

    @Override
    public void deposit(long amount) {
        if (active && amount > 0
                && balance <= Long.MAX_VALUE - amount) {
            balance += amount;
            System.out.println("Deposited: Rs. " + amount);
        } else {
            System.out.println("Invalid deposit or inactive account.");
        }
    }

    @Override
    public boolean withdraw(long amount) {
        if (active && amount > 0 && canWithdraw(amount)) {
            balance -= amount;
            System.out.println("Withdrawn: Rs. " + amount);
            return true;
        }

        System.out.println("Withdrawal failed.");
        return false;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Account{accountNumber='" + accountNumber
                + "', ownerName='" + ownerName
                + "', balance=" + balance + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account other)) {
            return false;
        }

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}