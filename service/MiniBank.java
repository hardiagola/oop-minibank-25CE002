package service;

import java.util.Scanner;

import model.Account;
import model.Customer;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.Premium;
import model.WithdrawRule;
import model.Command;

// Static import demonstration
import static util.Validator.isValidEmail;

import util.Validator;
import util.StatementFormatter;
import util.AnnotationValidator;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

import java.nio.file.Files;
import java.nio.file.Path;

import util.StatePersister;
import util.TransactionLog;
import util.ReportGenerator;
import util.Repository;

import java.util.ArrayList;
import java.util.Collections;

public class MiniBank {

    // Practical 1: Bank information record
    record BankInfo(String name, String branch) {}

    // Practical 1: Menu options
    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    // AutoCloseable resource for try-with-resources demo
    static class BankSession implements AutoCloseable {
        private final String sessionId;

        BankSession(String sessionId) {
            this.sessionId = sessionId;
            System.out.println("Session opened: " + sessionId);
        }

        @Override
        public void close() {
            System.out.println("Session closed: " + sessionId);
        }
    }

    public static void main(String[] args) throws Exception {

        // ========================================
        // PRACTICAL 1: BANK HEADER
        // ========================================
        BankInfo bank = new BankInfo(
                "MiniBank", "CHARUSAT Branch");

        System.out.println("================================");
        System.out.println("Welcome to " + bank.name());
        System.out.println("Branch: " + bank.branch());
        System.out.println("================================");

        // ========================================
        // PRACTICAL 2 & 3: CUSTOMERS
        // ========================================
        Customer.Address address1 = new Customer.Address(
                "Main Road", "Ahmedabad", "380001");

        Customer c1 = new Customer(
                "Riya Shah", "riya@gmail.com",
                "9876543210", address1);

        Customer c2 = new Customer(
                "Aarav Patel", "aarav@gmail.com",
                "9876543211",
                new Customer.Address(
                        "College Road", "Anand", "388001"));

        System.out.println("\n===== CUSTOMER DETAILS =====");
        System.out.println("Customer ID: " + c1.getCustomerId());
        System.out.println("Name: " + c1.getName());
        System.out.println("Email: " + c1.getEmail());
        System.out.println("Address: " + c1.getAddress());

        System.out.println("\nCustomer ID: " + c2.getCustomerId());
        System.out.println("Name: " + c2.getName());
        System.out.println("Address: " + c2.getAddress());

        // Practical 3: Clone customer
        Customer clonedCustomer = c1.clone();

        System.out.println("\n===== CUSTOMER CLONING =====");
        System.out.println("Original: " + c1.getName());
        System.out.println("Cloned: " + clonedCustomer.getName());
        System.out.println("Same object: " + (c1 == clonedCustomer));
        System.out.println("Same customer ID: "
                + c1.getCustomerId().equals(
                        clonedCustomer.getCustomerId()));

        // ========================================
        // PRACTICAL: ACCOUNT TYPES AND POLYMORPHISM
        // ========================================
        Account[] accounts = {
                new SavingsAccount("Riya Shah", 5000, 1000),
                new CurrentAccount("Aarav Patel", 1000, 5000),
                new FixedDepositAccount("Meera Desai", 10000)
        };

        System.out.println("\n===== INITIAL ACCOUNT DETAILS =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        // ========================================
        // TRANSACTABLE INTERFACE
        // ========================================
        System.out.println("\n===== TRANSACTIONS =====");

        // Savings: deposit then withdraw — success case
        try {
            accounts[0].deposit(2000);
            accounts[0].withdraw(1000);
            System.out.println("Savings withdrawal: success");
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds: shortfall Rs. "
                    + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("Savings transaction done.");
        }

        // Current account: deposit then withdraw — success case
        try {
            accounts[1].deposit(3000);
            accounts[1].withdraw(500);
            System.out.println("Current withdrawal: success");
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds: shortfall Rs. "
                    + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("Current transaction done.");
        }

        // Fixed deposit: withdrawal always fails — InsufficientFundsException
        try {
            accounts[2].withdraw(1000);
            System.out.println("Fixed deposit withdrawal: success");
        } catch (InsufficientFundsException e) {
            System.out.println("Fixed deposit withdrawal failed: "
                    + e.getMessage()
                    + " (shortfall Rs. " + e.getShortfall() + ")");
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("FD transaction done.");
        }

        // ========================================
        // PRACTICAL 3: equals() AND instanceof
        // ========================================
        System.out.println("\n===== ACCOUNT COMPARISON =====");

        System.out.println("Account 1 equals Account 2: "
                + accounts[0].equals(accounts[1]));

        System.out.println("Account 1 equals itself: "
                + accounts[0].equals(accounts[0]));

        Object obj = accounts[0];

        if (obj instanceof Account) {
            System.out.println("The object is an Account.");
        }

        // ========================================
        // INTERESTBEARING DEFAULT METHOD
        // ========================================
        System.out.println("\n===== YEARLY INTEREST =====");

        for (Account account : accounts) {
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Interest rate: "
                    + account.interestRate() + "%");
            System.out.printf("Yearly interest: Rs. %.2f%n",
                    account.yearlyInterest());
        }

        // ========================================
        // WITHDRAWRULE: ANONYMOUS CLASS
        // ========================================
        System.out.println("\n===== WITHDRAW RULE: ANONYMOUS CLASS =====");

        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.isActive()
                        && account.canWithdraw(amount);
            }
        };

        System.out.println("Allow savings withdrawal of Rs. 500: "
                + anonymousRule.allow(accounts[0], 500));

        // ========================================
        // WITHDRAWRULE: LAMBDA EXPRESSION
        // ========================================
        System.out.println("\n===== WITHDRAW RULE: LAMBDA =====");

        WithdrawRule lambdaRule =
                (account, amount) ->
                        account.isActive()
                        && amount > 0
                        && amount <= 2000
                        && account.canWithdraw(amount);

        System.out.println("Allow withdrawal of Rs. 1500: "
                + lambdaRule.allow(accounts[0], 1500));

        System.out.println("Allow withdrawal of Rs. 3000: "
                + lambdaRule.allow(accounts[0], 3000));

        // ========================================
        // PREMIUM MARKER INTERFACE
        // ========================================
        System.out.println("\n===== PREMIUM ACCOUNT CHECK =====");

        for (Account account : accounts) {
            if (account instanceof Premium) {
                System.out.println(account.getOwnerName()
                        + " has a Premium account.");
            } else {
                System.out.println(account.getOwnerName()
                        + " does not have a Premium account.");
            }
        }

        // ========================================
        // VALIDATOR TESTS
        // ========================================
        System.out.println("\n===== VALIDATOR TESTS =====");

        System.out.println("Valid mobile: "
                + Validator.isValidMobile("9876543210"));
        System.out.println("Invalid mobile: "
                + Validator.isValidMobile("12345"));

        // Static import: no Validator. prefix needed
        System.out.println("Valid email: "
                + isValidEmail("riya@gmail.com"));
        System.out.println("Invalid email: "
                + isValidEmail("riya@"));

        System.out.println("Valid PAN: "
                + Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN: "
                + Validator.isValidPan("ABC123"));

        System.out.println("Valid IFSC: "
                + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("Invalid IFSC: "
                + Validator.isValidIfsc("SBI123"));

        // ========================================
        // COMMAND PARSING
        // ========================================
        System.out.println("\n===== COMMAND PARSER =====");

        try {
            Command command =
                    CommandParser.parse("DEPOSIT AC0001 500");

            System.out.println("Type: " + command.type());
            System.out.println("Account: "
                    + command.accountNumber());
            System.out.println("Amount: " + command.amount());

        } catch (IllegalArgumentException e) {
            System.out.println("Command error: " + e.getMessage());
        }

        // ========================================
        // ACCOUNT STATEMENT
        // ========================================
        System.out.println("\n===== ACCOUNT STATEMENT =====");
        System.out.println(
                StatementFormatter.buildStatement(accounts[0]));

        // ========================================
        // ANNOTATION VALIDATOR
        // ========================================
        System.out.println("\n===== ANNOTATION VALIDATOR =====");

        // FixedDepositAccount with balance 0 violates @Positive (must be > 0)
        FixedDepositAccount testAccount =
                new FixedDepositAccount("Test User", 0);

        String[] errors = AnnotationValidator.validate(testAccount);

        if (errors.length == 0) {
            System.out.println("No validation errors.");
        } else {
            for (String error : errors) {
                System.out.println("Validation error -> " + error);
            }
        }

        // ========================================
        // TRANSFER (try-catch-finally + re-throw)
        // ========================================
        System.out.println("\n===== TRANSFER =====");

        // Valid transfer from savings to current
        try {
            accounts[0].transfer(accounts[1], 500);
            System.out.println("Transfer successful.");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // Transfer that fails (FD account cannot withdraw)
        try {
            accounts[2].transfer(accounts[0], 500);
            System.out.println("Transfer successful.");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // ========================================
        // INVALID AMOUNT (InvalidAmountException demo)
        // ========================================
        System.out.println("\n===== INVALID AMOUNT DEMO =====");

        try {
            accounts[0].deposit(-500);
        } catch (InvalidAmountException e) {
            System.out.println("Deposit error: " + e.getMessage());
        }

        try {
            accounts[0].withdraw(0);
        } catch (InsufficientFundsException e) {
            System.out.println("Withdraw error: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Withdraw error: " + e.getMessage());
        }

        // ========================================
        // TRY-WITH-RESOURCES (AutoCloseable)
        // ========================================
        System.out.println("\n===== TRY-WITH-RESOURCES =====");

        try (BankSession session = new BankSession("TXN-DEPOSIT-001")) {
            accounts[0].deposit(1000);
            System.out.println("Deposited Rs. 1000 inside session.");
        } catch (InvalidAmountException e) {
            System.out.println("Session error: " + e.getMessage());
        }

        // ========================================
        // THREAD DEMO: RACE CONDITION (no sync)
        // ========================================
        System.out.println("\n===== THREAD DEMO: RACE CONDITION =====");
        System.out.println("(Unsynchronized long[] counter — result will be wrong)");

        final long[] unsafeBalance = {0};
        final int threadCount    = 10;
        final int depositsEach   = 1000;
        final long depositAmt    = 10L;
        final long expectedTotal = threadCount * depositsEach * depositAmt;

        Thread[] raceThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            raceThreads[idx] = new Thread(() -> {
                for (int j = 0; j < depositsEach; j++) {
                    unsafeBalance[0] += depositAmt;  // NOT thread-safe
                }
                System.out.println(Thread.currentThread().getName()
                        + " finished (unsafe).");
            }, "RaceThread-" + (i + 1));
        }

        for (Thread t : raceThreads) {
            System.out.println(t.getName() + " started.");
            t.start();
        }
        for (Thread t : raceThreads) {
            t.join();
        }

        System.out.println("Expected balance : Rs. " + expectedTotal);
        System.out.println("Actual   (unsafe): Rs. " + unsafeBalance[0]);
        System.out.println("Race condition   : "
                + (unsafeBalance[0] != expectedTotal
                        ? "DETECTED (balance is wrong!)"
                        : "Not visible this run (try more threads)"));

        // ========================================
        // THREAD DEMO: SYNCHRONIZED (correct)
        // ========================================
        System.out.println("\n===== THREAD DEMO: SYNCHRONIZED =====");
        System.out.println("(AccountWorker + synchronized lock — result will be correct)");

        SavingsAccount sharedAccount =
                new SavingsAccount("Thread Test", 0, 0);

        Thread[] workers = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            AccountWorker worker =
                    new AccountWorker(sharedAccount, depositsEach, depositAmt);
            workers[i] = new Thread(worker, "Worker-" + (i + 1));
        }

        for (Thread t : workers) {
            System.out.println(t.getName() + " started.");
            t.start();
        }
        for (Thread t : workers) {
            t.join();
        }

        System.out.println("Expected balance      : Rs. " + expectedTotal);
        System.out.println("Actual (synchronized) : Rs. " + sharedAccount.getBalance());
        System.out.println("Result correct        : "
                + (sharedAccount.getBalance() == expectedTotal));

        // ========================================
        // TRANSACTION PROCESSOR (ExecutorService)
        // ========================================
        System.out.println("\n===== TRANSACTION PROCESSOR =====");

        TransactionProcessor processor = new TransactionProcessor();
        SavingsAccount tpAccount = new SavingsAccount("TP Account", 10000, 0);

        // Submit 6 tasks alternating deposit / withdraw
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            processor.submit(() -> {
                try {
                    if (taskId % 2 == 0) {
                        tpAccount.deposit(500);
                    } else {
                        tpAccount.withdraw(200);
                    }
                    System.out.println("Task-" + taskId + " done by "
                            + Thread.currentThread().getName());
                } catch (Exception e) {
                    System.out.println("Task-" + taskId
                            + " error: " + e.getMessage());
                }
            });
        }

        processor.stop();
        System.out.println("Final TP account balance: Rs. "
                + tpAccount.getBalance());

        // ========================================
        // PRODUCER-CONSUMER (wait / notify)
        // ========================================
        System.out.println("\n===== PRODUCER-CONSUMER =====");

        final int BUFFER_SIZE = 3;
        final String[] txnBuffer  = new String[BUFFER_SIZE];
        final int[]    bufSize    = {0};
        final Object   bufMonitor = new Object();

        Runnable producerTask = () -> {
            String[] txns = {
                "DEPOSIT:500", "WITHDRAW:200", "DEPOSIT:1000",
                "DEPOSIT:300", "WITHDRAW:100"
            };
            for (String txn : txns) {
                synchronized (bufMonitor) {
                    while (bufSize[0] == BUFFER_SIZE) {
                        try {
                            System.out.println("Producer waiting (buffer full)…");
                            bufMonitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    txnBuffer[bufSize[0]++] = txn;
                    System.out.println("Produced : " + txn
                            + "  | buffer=" + bufSize[0]);
                    bufMonitor.notify();
                }
                try { Thread.sleep(50); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        };

        Runnable consumerTask = () -> {
            for (int i = 0; i < 5; i++) {
                String txn;
                synchronized (bufMonitor) {
                    while (bufSize[0] == 0) {
                        try {
                            System.out.println("Consumer waiting (buffer empty)…");
                            bufMonitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    txn = txnBuffer[--bufSize[0]];
                    System.out.println("Consumed : " + txn
                            + "  | buffer=" + bufSize[0]);
                    bufMonitor.notify();
                }
                try { Thread.sleep(120); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        };

        Thread producerThread = new Thread(producerTask, "Producer");
        Thread consumerThread = new Thread(consumerTask, "Consumer");
        producerThread.start();
        consumerThread.start();
        producerThread.join();
        consumerThread.join();
        System.out.println("Producer-Consumer finished.");

        // ========================================
        // DEADLOCK DEMO (opposite lock order)
        // ========================================
        System.out.println("\n===== DEADLOCK DEMO =====");

        SavingsAccount dlA = new SavingsAccount("DL-Account-A", 5000, 0);
        SavingsAccount dlB = new SavingsAccount("DL-Account-B", 5000, 0);

        // Thread-1 locks A then B; Thread-2 locks B then A → circular wait
        Thread tDeadlock1 = new Thread(() -> {
            synchronized (dlA) {
                System.out.println(Thread.currentThread().getName()
                        + " locked A, now waiting for B…");
                try { Thread.sleep(200); }
                catch (InterruptedException e) { return; }
                synchronized (dlB) {
                    System.out.println(Thread.currentThread().getName()
                            + " locked B — transfer A→B done.");
                }
            }
        }, "Deadlock-T1(A→B)");

        Thread tDeadlock2 = new Thread(() -> {
            synchronized (dlB) {
                System.out.println(Thread.currentThread().getName()
                        + " locked B, now waiting for A…");
                try { Thread.sleep(200); }
                catch (InterruptedException e) { return; }
                synchronized (dlA) {
                    System.out.println(Thread.currentThread().getName()
                            + " locked A — transfer B→A done.");
                }
            }
        }, "Deadlock-T2(B→A)");

        // Mark as daemon so the JVM is not blocked if deadlock occurs
        tDeadlock1.setDaemon(true);
        tDeadlock2.setDaemon(true);
        tDeadlock1.start();
        tDeadlock2.start();

        Thread.sleep(1000); // Give them time to deadlock

        if (tDeadlock1.isAlive() && tDeadlock2.isAlive()) {
            System.out.println("DEADLOCK DETECTED! "
                    + "Both threads are still blocked (circular wait).");
        } else {
            System.out.println("No deadlock observed this run.");
        }

        // ========================================
        // DEADLOCK FIX (consistent lock order)
        // ========================================
        System.out.println("\n===== DEADLOCK FIX: CONSISTENT LOCK ORDER =====");

        // Use FRESH accounts — dlA/dlB are still held by daemon threads
        SavingsAccount fixA = new SavingsAccount("Fix-Account-A", 5000, 0);
        SavingsAccount fixB = new SavingsAccount("Fix-Account-B", 5000, 0);

        // Always acquire the lock of the account with the SMALLER accountNumber first
        Account fixFirst  = fixA.getAccountNumber()
                .compareTo(fixB.getAccountNumber()) <= 0 ? fixA : fixB;
        Account fixSecond = (fixFirst == fixA) ? fixB : fixA;

        Thread tFix1 = new Thread(() -> {
            synchronized (fixFirst) {
                synchronized (fixSecond) {
                    System.out.println(Thread.currentThread().getName()
                            + " — transfer A→B completed safely.");
                }
            }
        }, "Fixed-T1(A→B)");

        Thread tFix2 = new Thread(() -> {
            synchronized (fixFirst) {
                synchronized (fixSecond) {
                    System.out.println(Thread.currentThread().getName()
                            + " — transfer B→A completed safely.");
                }
            }
        }, "Fixed-T2(B→A)");

        tFix1.start();
        tFix2.start();
        tFix1.join();
        tFix2.join();
        System.out.println("Both fixed threads completed — no deadlock!");

        // ========================================
        // PERSISTENCE: SAVE, LOG & REPORT
        // ========================================
        System.out.println("\n===== PERSISTENCE: SAVE & LOAD =====");

        Path dataDir   = Path.of("data");
        Path logsDir   = dataDir.resolve("logs");
        Path acctFile  = dataDir.resolve("accounts.dat");
        Path logFile   = logsDir.resolve("transactions.log");
        Path reportFile = dataDir.resolve("report.txt");

        Files.createDirectories(logsDir);

        // Save accounts array to disk
        StatePersister.save(accounts, acctFile);

        // Append transaction entries to the log
        TransactionLog.append(logFile, "DEPOSIT:2000");
        TransactionLog.append(logFile, "WITHDRAW:500");
        TransactionLog.append(logFile, "DEPOSIT:1500");
        TransactionLog.append(logFile, "WITHDRAW:300");
        TransactionLog.append(logFile, "DEPOSIT:3000");
        System.out.println("5 entries written to: " + logFile);

        // Reload accounts and confirm data survived
        Account[] loadedAccounts = StatePersister.load(acctFile);
        System.out.println("\n--- Reloaded Accounts ---");
        for (Account acc : loadedAccounts) {
            System.out.println(acc);
        }

        // Generate and print end-of-day report
        System.out.println();
        ReportGenerator.generate(logsDir, reportFile);

        // ========================================
        // ACCOUNT STORE (HashMap + Comparator)
        // ========================================
        System.out.println("\n===== ACCOUNT STORE =====");

        AccountStore store = new AccountStore();

        // Add several accounts to the store
        store.add(new SavingsAccount("Alice Sharma",  8000, 500));
        store.add(new SavingsAccount("Bob Mehta",     2500, 500));
        store.add(new CurrentAccount("Carol Joshi",   15000, 3000));
        store.add(new FixedDepositAccount("David Rao", 50000));
        store.add(new SavingsAccount("Eve Nair",       500,   500));

        System.out.println("Store size: " + store.size() + " accounts");

        // get() by account number
        ArrayList<Account> all = store.getAll();
        if (!all.isEmpty()) {
            String firstKey = all.get(0).getAccountNumber();
            System.out.println("\nget(\"" + firstKey + "\"): "
                    + store.get(firstKey));
        }

        // sortedByBalance() — uses Comparator (highest balance first)
        System.out.println("\n--- Sorted by Balance (Comparator, highest first) ---");
        for (Account acc : store.sortedByBalance()) {
            System.out.printf("  %-12s | %-16s | Rs. %,d%n",
                    acc.getAccountNumber(),
                    acc.getOwnerName(),
                    acc.getBalance());
        }

        // Natural order via compareTo() — Collections.sort uses Comparable
        System.out.println("\n--- Natural Order (Comparable, by accountNumber) ---");
        ArrayList<Account> natural = store.getAll();
        Collections.sort(natural);
        for (Account acc : natural) {
            System.out.printf("  %-12s | %s%n",
                    acc.getAccountNumber(), acc.getOwnerName());
        }

        // Log a transaction and show history
        store.logTransaction("DEPOSIT:1000 → " + all.get(0).getAccountNumber());
        System.out.println("\n--- Transaction History ---");
        for (String entry : store.getTransactionHistory()) {
            System.out.println("  " + entry);
        }

        // ConcurrentHashMap in TransactionProcessor
        System.out.println("\n--- ConcurrentHashMap in TransactionProcessor ---");
        TransactionProcessor tp2 = new TransactionProcessor();
        for (Account acc : store.getAll()) {
            tp2.registerAccount(acc);
        }
        System.out.println("Registered " + tp2.getSharedAccounts().size()
                + " accounts in ConcurrentHashMap.");
        tp2.stop();

        // ========================================
        // REPOSITORY<T> (Generics + Reflection)
        // ========================================
        System.out.println("\n===== REPOSITORY<Account> =====");

        Repository<Account> repo = new Repository<>();

        // saveAll() — @SafeVarargs, saves all three demo accounts at once
        repo.saveAll(accounts[0], accounts[1], accounts[2]);
        System.out.println("Saved " + repo.size() + " accounts via saveAll().");

        // findById() — key resolved via @Id reflection
        String targetId = accounts[0].getAccountNumber();
        Account found = repo.findById(targetId);
        System.out.println("findById(\"" + targetId + "\"): " + found);

        // findAll() — print every stored account
        System.out.println("\n--- All accounts in Repository ---");
        for (Account acc : repo.findAll()) {
            System.out.printf("  %-10s | %-20s | Rs. %,d%n",
                    acc.getAccountNumber(),
                    acc.getOwnerName(),
                    acc.getBalance());
        }

        // delete()
        boolean removed = repo.delete(accounts[1].getAccountNumber());
        System.out.println("\ndelete(\"" + accounts[1].getAccountNumber() + "\"): "
                + removed + " | remaining: " + repo.size());

        // Bounded type parameter: <N extends Number> average
        Long[] balances = new Long[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            balances[i] = accounts[i].getBalance();
        }
        double avg = Repository.average(balances);
        System.out.printf("Average balance (bounded <N extends Number>): Rs. %.2f%n",
                avg);

        // ========================================
        // ORIGINAL MENU
        // ========================================
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a number from 1 to 5.");
                sc.nextLine();
                choice = 0;
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine(); // flush leftover newline after nextInt()

            switch (choice) {

                case 1 -> {
                    // Open Account
                    System.out.print("Enter owner name       : ");
                    String ownerName = sc.nextLine().trim();
                    System.out.println("Account type: 1.Savings  2.Current  3.Fixed Deposit");
                    System.out.print("Choose type (1/2/3)    : ");
                    int accType = sc.nextInt(); sc.nextLine();
                    System.out.print("Opening balance (Rs.)  : ");
                    long openBal = sc.nextLong(); sc.nextLine();
                    try {
                        Account newAcc = switch (accType) {
                            case 2  -> new CurrentAccount(ownerName, openBal, 1000);
                            case 3  -> new FixedDepositAccount(ownerName, openBal);
                            default -> new SavingsAccount(ownerName, openBal, 0);
                        };
                        store.add(newAcc);
                        System.out.println("Account created: " + newAcc.getAccountNumber()
                                + " | Owner: " + newAcc.getOwnerName()
                                + " | Balance: Rs. " + newAcc.getBalance());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                case 2 -> {
                    // Deposit
                    System.out.print("Enter account number : ");
                    String depNum = sc.nextLine().trim();
                    Account depAcc = store.get(depNum);
                    if (depAcc == null) {
                        System.out.println("Account not found: " + depNum);
                    } else {
                        System.out.print("Enter deposit amount : Rs. ");
                        long depAmt = sc.nextLong(); sc.nextLine();
                        try {
                            depAcc.deposit(depAmt);
                            store.logTransaction("DEPOSIT:" + depAmt + " -> " + depNum);
                            System.out.println("Success! New balance: Rs. " + depAcc.getBalance());
                        } catch (InvalidAmountException e) {
                            System.out.println("Deposit failed: " + e.getMessage());
                        }
                    }
                }

                case 3 -> {
                    // Withdraw
                    System.out.print("Enter account number : ");
                    String wdNum = sc.nextLine().trim();
                    Account wdAcc = store.get(wdNum);
                    if (wdAcc == null) {
                        System.out.println("Account not found: " + wdNum);
                    } else {
                        System.out.print("Enter withdrawal amount : Rs. ");
                        long wdAmt = sc.nextLong(); sc.nextLine();
                        try {
                            wdAcc.withdraw(wdAmt);
                            store.logTransaction("WITHDRAW:" + wdAmt + " <- " + wdNum);
                            System.out.println("Success! New balance: Rs. " + wdAcc.getBalance());
                        } catch (InsufficientFundsException | InvalidAmountException e) {
                            System.out.println("Withdrawal failed: " + e.getMessage());
                        }
                    }
                }

                case 4 -> {
                    // Transfer
                    System.out.print("From account number : ");
                    String fromNum = sc.nextLine().trim();
                    System.out.print("To   account number : ");
                    String toNum   = sc.nextLine().trim();
                    Account fromAcc = store.get(fromNum);
                    Account toAcc   = store.get(toNum);
                    if (fromAcc == null) {
                        System.out.println("Source account not found: " + fromNum);
                    } else if (toAcc == null) {
                        System.out.println("Destination account not found: " + toNum);
                    } else {
                        System.out.print("Transfer amount : Rs. ");
                        long trAmt = sc.nextLong(); sc.nextLine();
                        try {
                            fromAcc.transfer(toAcc, trAmt);
                            store.logTransaction("TRANSFER:" + trAmt + " " + fromNum + "->" + toNum);
                            System.out.println("Transfer successful.");
                            System.out.println("  " + fromNum + " balance: Rs. " + fromAcc.getBalance());
                            System.out.println("  " + toNum   + " balance: Rs. " + toAcc.getBalance());
                        } catch (BankException e) {
                            System.out.println("Transfer failed: " + e.getMessage());
                        }
                    }
                }

                case 5 -> System.out.println("Thank you for using MiniBank. Goodbye!");

                default -> System.out.println("Invalid choice. Please select 1 to 5.");
            }

        } while (choice != 5);

        System.out.println("Hardi Agola - 25CE002");
        sc.close();
    }
}