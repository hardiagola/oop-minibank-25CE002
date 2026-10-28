package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

import model.annotation.Id;
import model.annotation.Positive;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public abstract class Account
        implements Transactable, InterestBearing, Serializable,
                   Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;

    // transient: Object is not Serializable; readObject() reinitialises it
    private transient Object lock = new Object();

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
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }
        synchronized (lock) {
            if (!active) {
                throw new InvalidAmountException("Account is inactive.");
            }
            if (balance > Long.MAX_VALUE - amount) {
                throw new InvalidAmountException(
                        "Deposit would overflow balance.");
            }
            balance += amount;
        }
        System.out.println(Thread.currentThread().getName()
                + " deposited: Rs. " + amount);
    }

    @Override
    public void withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero.");
        }
        synchronized (lock) {
            if (!active) {
                throw new InvalidAmountException("Account is inactive.");
            }
            if (!canWithdraw(amount)) {
                long shortfall = Math.max(0, amount - balance);
                throw new InsufficientFundsException(
                        "Insufficient funds for withdrawal.", shortfall);
            }
            balance -= amount;
        }
        System.out.println(Thread.currentThread().getName()
                + " withdrew: Rs. " + amount);
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            this.withdraw(amount);
            to.deposit(amount);
        } catch (InsufficientFundsException | InvalidAmountException e) {
            throw e;
        } finally {
            System.out.println("Transfer attempt completed.");
        }
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

    // Natural ordering by account number (AC0001 < AC0002 …)
    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    // Reinitialise the transient lock after deserialization
    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        lock = new Object();
    }
}