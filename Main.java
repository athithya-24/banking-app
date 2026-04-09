import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BankService bank = new BankService();

        System.out.println(" Login");

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (!bank.login(username, password)) {
            System.out.println("Invalid credentials!");
            System.exit(0);
        }

        System.out.println("Login successful\n");

        while (true) {

            System.out.println("\n1. Create account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Check balance");
            System.out.println("6. Exit");
            System.out.println("7. View transaction history");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter initial amount: ");
                    double bal = sc.nextDouble();

                    System.out.print("Enter type: ");
                    String type = sc.next();

                    Account acc = bank.createAcc(name, bal, type);

                    if (acc != null) {
                        System.out.println("Account created, ID: " + acc.getAccId());
                    } else {
                        System.out.println("Error creating account");
                    }
                    break;

                case 2:
                    System.out.print("Enter account ID: ");
                    int dId = sc.nextInt();

                    System.out.print("Enter amount: ");
                    double dAmt = sc.nextDouble();

                    bank.deposit(dId, dAmt);
                    break;

                case 3:
                    System.out.print("Enter account ID: ");
                    int wId = sc.nextInt();

                    System.out.print("Enter amount: ");
                    double wAmt = sc.nextDouble();

                    bank.withdraw(wId, wAmt);
                    break;

                case 4:
                    System.out.print("From ID: ");
                    int from = sc.nextInt();

                    System.out.print("To ID: ");
                    int to = sc.nextInt();

                    System.out.print("Amount: ");
                    double amt = sc.nextDouble();

                    bank.transfer(from, to, amt);
                    break;

                case 5:
                    System.out.print("Enter account ID: ");
                    int cId = sc.nextInt();

                    bank.checkBal(cId);
                    break;

                case 6:
                    System.out.println("Exit");
                    sc.close();
                    System.exit(0);

                case 7:
                    System.out.print("Enter account ID: ");
                    int tId = sc.nextInt();

                    bank.showTransactions(tId);
                    break;

                default:
                    System.out.println("Invalid choiceZ");
            }
        }
    }
}