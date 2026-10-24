
import java.util.Scanner;

public class minibank {

    // Bank information record
    record BankInfo(String name, String branch) {}

    // Menu options
    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    public static void main(String[] args) {

        // Bank details
        BankInfo bank = new BankInfo("MiniBank", "CHARUSAT Branch");

        // Display application header
        System.out.println("================================");
        System.out.println("Welcome to " + bank.name());
        System.out.println("Branch: " + bank.branch());
        System.out.println("================================");

        Scanner sc = new Scanner(System.in);

        int choice;

        // Repeat menu until user selects EXIT
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            // Handle the selected option
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
