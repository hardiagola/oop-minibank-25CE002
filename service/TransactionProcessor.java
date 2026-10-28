package service;

import model.Account;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionProcessor {

    private final ExecutorService executor;

    // ConcurrentHashMap for thread-safe shared account access
    private final ConcurrentHashMap<String, Account> sharedAccounts =
            new ConcurrentHashMap<>();

    public TransactionProcessor() {
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Processor forced shutdown.");
            } else {
                System.out.println("Processor stopped cleanly.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void registerAccount(Account account) {
        sharedAccounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }
}
