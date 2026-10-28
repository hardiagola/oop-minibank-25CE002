
import java.util.Scanner;

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
        BankInfo bank = new BankInfo("MiniBank", "CHARUSAT Branch");

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
        // PRACTICAL 2 & 3: ACCOUNTS
        // ========================================
        // Account is now abstract, so use its subclasses.
        Account[] accounts = {
                new SavingsAccount("Riya Shah", 5000, 1000),
                new CurrentAccount("Aarav Patel", 1000, 5000),
                new FixedDepositAccount("Meera Desai", 10000)
        };

        System.out.println("\n===== INITIAL BALANCES =====");
        for (Account account : accounts) {
            System.out.println(account);
        }

        // Demonstrate deposits and withdrawals
        System.out.println("\n===== TRANSACTIONS =====");

        accounts[0].deposit(2000);
        System.out.println("Savings withdrawal successful: "
                + accounts[0].withdraw(1000));

        accounts[1].deposit(3000);
        System.out.println("Current withdrawal successful: "
                + accounts[1].withdraw(500));

        System.out.println("Fixed deposit withdrawal successful: "
                + accounts[2].withdraw(12000));

        // Practical 3: toString()
        System.out.println("\n===== ACCOUNT DETAILS =====");
        for (Account account : accounts) {
            System.out.println(account.toString());
        }

        // Practical 3: equals()
        System.out.println("\n===== ACCOUNT COMPARISON =====");
        System.out.println("Account 1 equals Account 2: "
                + accounts[0].equals(accounts[1]));

        System.out.println("Account 1 equals itself: "
                + accounts[0].equals(accounts[0]));

        // Practical 3: instanceof
        System.out.println("\n===== INSTANCEOF TEST =====");
        Object obj = accounts[0];

        if (obj instanceof Account) {
            System.out.println("The object is an Account.");
        } else {
            System.out.println("The object is not an Account.");
        }

        // ========================================
        // LATEST PRACTICAL: ABSTRACTION & POLYMORPHISM
        // ========================================
        System.out.println("\n===== ACCOUNT TYPES AND INTEREST =====");

        for (Account account : accounts) {
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Account: " + account.getAccountNumber());
            System.out.println("Interest rate: "
                    + account.interestRate() + "%");
        }

        System.out.println("\n===== ACCOUNT TYPE CHECK =====");

        for (Account account : accounts) {
            if (account instanceof SavingsAccount savings) {
                System.out.println(
                        savings.getOwnerName()
                        + " has minimum balance Rs. "
                        + savings.getMinBalance());

            } else if (account instanceof CurrentAccount current) {
                System.out.println(
                        current.getOwnerName()
                        + " has overdraft limit Rs. "
                        + current.getOverdraftLimit());

            } else if (account instanceof FixedDepositAccount) {
                System.out.println(
                        account.getOwnerName()
                        + " has a locked fixed deposit.");
            }
        }

        // ========================================
        // PRACTICAL: VALIDATOR TESTS
        // ========================================
        System.out.println("\n===== VALIDATOR TESTS =====");

        System.out.println("Valid mobile: "
                + Validator.isValidMobile("9876543210"));
        System.out.println("Invalid mobile: "
                + Validator.isValidMobile("12345"));

        System.out.println("Valid email: "
                + Validator.isValidEmail("riya@gmail.com"));
        System.out.println("Invalid email: "
                + Validator.isValidEmail("riya@"));

        System.out.println("Valid PAN: "
                + Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN: "
                + Validator.isValidPan("ABC123"));

        System.out.println("Valid IFSC: "
                + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("Invalid IFSC: "
                + Validator.isValidIfsc("SBI123"));

        // ========================================
        // PRACTICAL: COMMAND PARSING
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
        // PRACTICAL: ACCOUNT STATEMENT
        // ========================================
        System.out.println("\n===== ACCOUNT STATEMENT =====");
        System.out.println(
                StatementFormatter.buildStatement(accounts[0]));

        // ========================================
        // PRACTICAL 1: ORIGINAL MENU
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
