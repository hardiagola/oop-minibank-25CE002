
import java.util.Scanner;

public class minibank {

    record BankInfo(String name, String branch) {
    }

    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    public static void main(String[] args) {

        BankInfo bank = new BankInfo("MiniBank", "CHARUSAT Branch");

        Customer.Address address = new Customer.Address("Main Road", "Ahmedabad", "380001");

        Customer customer = new Customer("Riya Shah", "riya@gmail.com", "9876543210", address);

        System.out.println("Customer: " + customer.getName());
        System.out.println("Address: " + customer.getAddress().getLine());
        System.out.println("City: " + customer.getAddress().getCity());
        System.out.println("Pincode: " + customer.getAddress().getPincode());

        System.out.println("================================");
        System.out.println("Welcome to " + bank.name());
        System.out.println("Branch: " + bank.branch());
        System.out.println("================================");

        // Practical 2: Create customers
        Customer c1 = new Customer(
                "Riya Shah", "riya@gmail.com", "9876543210");
        Customer c2 = new Customer(
                "Aarav Patel", "aarav@gmail.com", "9876543211");

        System.out.println("\nCustomer IDs:");
        System.out.println(c1.getCustomerId() + " - " + c1.getName());
        System.out.println(c2.getCustomerId() + " - " + c2.getName());

        // Practical 2: Create three accounts
        Account[] accounts = {
                new Account("Riya Shah", 5000),
                new Account("Aarav Patel"),
                new Account("Meera Desai", 10000)
        };

        // Demonstrate deposits and withdrawals
        accounts[0].deposit(2000);
        accounts[0].withdraw(1000);
        accounts[1].deposit(3000);
        accounts[1].withdraw(500);
        accounts[2].withdraw(12000);

        System.out.println("\nAccount Balances:");
        for (Account account : accounts) {
            System.out.println(
                    account.getAccountNumber() + " | "
                            + account.getOwnerName() + " | Rs. "
                            + account.getBalance());
        }

        Scanner sc = new Scanner(System.in);
        int choice;

        // Practical 1: Original menu loop
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

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

        sc.close();
    }
}
