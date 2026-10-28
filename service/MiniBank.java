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

    public static void main(String[] args) {

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

        accounts[0].deposit(2000);
        System.out.println("Savings withdrawal: "
                + accounts[0].withdraw(1000));

        accounts[1].deposit(3000);
        System.out.println("Current withdrawal: "
                + accounts[1].withdraw(500));

        System.out.println("Fixed deposit withdrawal: "
                + accounts[2].withdraw(1000));

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
                System.out.println(
                        "Please enter a number from 1 to 5.");
                sc.nextLine();
                choice = 0;
                continue;
            }

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println(
                        "Open Account - to be implemented in a later lab.");
                case 2 -> System.out.println(
                        "Deposit - to be implemented in a later lab.");
                case 3 -> System.out.println(
                        "Withdraw - to be implemented in a later lab.");
                case 4 -> System.out.println(
                        "Transfer - to be implemented in a later lab.");
                case 5 -> System.out.println(
                        "Thank you for using MiniBank. Goodbye!");
                default -> System.out.println(
                        "Invalid choice. Please select 1 to 5.");
            }

        } while (choice != 5);

        System.out.println("Hardi Agola - 25CE002");
        sc.close();
    }
}