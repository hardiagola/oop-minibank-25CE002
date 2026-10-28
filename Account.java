
import java.util.Objects;

public abstract class Account {

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    // Constructor with owner name and opening balance
    public Account(String ownerName, long openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    // Constructor with owner name only
    public Account(String ownerName) {
        this(ownerName, 0);
    }

    // Abstract methods implemented by subclasses
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    // Deposit money
    public void deposit(long amount) {
        if (active && amount > 0) {
            balance += amount;
            System.out.println("Deposited: Rs. " + amount);
        } else {
            System.out.println(
                    "Invalid deposit or inactive account.");
        }
    }

    // Withdraw money according to subclass rules
    public boolean withdraw(long amount) {
        if (active && amount > 0 && canWithdraw(amount)) {
            balance -= amount;
            System.out.println("Withdrawn: Rs. " + amount);
            return true;
        }

        System.out.println("Withdrawal failed.");
        return false;
    }

    // Getter methods
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

    // Practical 3: Readable account details
    @Override
    public String toString() {
        return "Account{accountNumber='" + accountNumber
                + "', ownerName='" + ownerName
                + "', balance=" + balance + "}";
    }

    // Accounts are equal when account numbers match
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account other = (Account) o;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
