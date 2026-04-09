import java.sql.*; 

class BankService {

    public Account createAcc(String name, double balance, String type) {
        try (Connection conn = DBConnection.getConnection()) {

            String sql = "INSERT INTO accounts (acc_name, balance, acc_type) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setDouble(2, balance);
            ps.setString(3, type);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Account(id, name, balance, type);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account findAcc(int accId) {
    try (Connection conn = DBConnection.getConnection()) {

        String sql = "SELECT * FROM accounts WHERE acc_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, accId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Account(
                rs.getInt("acc_id"),
                rs.getString("acc_name"),
                rs.getDouble("balance"),
                rs.getString("acc_type")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

    public void deposit(int accId, double amount) {

    if (amount <= 0) {
        System.out.println("Invalid amount");
        return;
    }

    try (Connection conn = DBConnection.getConnection()) {

        String sql = "UPDATE accounts SET balance = balance + ? WHERE acc_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setDouble(1, amount);
        ps.setInt(2, accId);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Amount deposited");

            String logSql = "INSERT INTO transactions (acc_id, type, amount) VALUES (?, ?, ?)";
            PreparedStatement logPs = conn.prepareStatement(logSql);

            logPs.setInt(1, accId);
            logPs.setString(2, "DEPOSIT");
            logPs.setDouble(3, amount);

            logPs.executeUpdate();

        } else {
            System.out.println("Account not found");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void withdraw(int accId, double amount) {

    if (amount <= 0) {
        System.out.println("Invalid amount");
        return;
    }

    try (Connection conn = DBConnection.getConnection()) {

        String checkSql = "SELECT balance FROM accounts WHERE acc_id = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, accId);

        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            double currentBalance = rs.getDouble("balance");

            if (currentBalance >= amount) {
                String updateSql = "UPDATE accounts SET balance = balance - ? WHERE acc_id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);

                updatePs.setDouble(1, amount);
                updatePs.setInt(2, accId);

                updatePs.executeUpdate();

                System.out.println("Withdrawal successful");

                String logSql = "INSERT INTO transactions (acc_id, type, amount) VALUES (?, ?, ?)";
                PreparedStatement logPs = conn.prepareStatement(logSql);

                logPs.setInt(1, accId);
                logPs.setString(2, "WITHDRAW");
                logPs.setDouble(3, amount);

                logPs.executeUpdate();

            } else {
                System.out.println("Insufficient balance");
            }

        } else {
            System.out.println("Account not found");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void transfer(int fromId, int toId, double amount) {

    if (amount <= 0) {
        System.out.println("Invalid amount");
        return;
    }

    Connection conn = null;

    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); 

        String checkSql = "SELECT balance FROM accounts WHERE acc_id = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, fromId);
        ResultSet rs = checkPs.executeQuery();

        if (!rs.next()) {
            System.out.println("Sender account not found");
            return;
        }

        double senderBalance = rs.getDouble("balance");

        if (senderBalance < amount) {
            System.out.println("Insufficient balance");
            return;
        }

        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE acc_id = ?";
        PreparedStatement withdrawPs = conn.prepareStatement(withdrawSql);
        withdrawPs.setDouble(1, amount);
        withdrawPs.setInt(2, fromId);
        withdrawPs.executeUpdate();

        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE acc_id = ?";
        PreparedStatement depositPs = conn.prepareStatement(depositSql);
        depositPs.setDouble(1, amount);
        depositPs.setInt(2, toId);

        int rows = depositPs.executeUpdate();

        if (rows == 0) {
            System.out.println("Receiver account not found");
            conn.rollback();
            return;
        }

        String logSql = "INSERT INTO transactions (acc_id, type, amount) VALUES (?, ?, ?)";

        PreparedStatement logPs1 = conn.prepareStatement(logSql);
        logPs1.setInt(1, fromId);
        logPs1.setString(2, "TRANSFER_OUT");
        logPs1.setDouble(3, amount);
        logPs1.executeUpdate();

        PreparedStatement logPs2 = conn.prepareStatement(logSql);
        logPs2.setInt(1, toId);
        logPs2.setString(2, "TRANSFER_IN");
        logPs2.setDouble(3, amount);
        logPs2.executeUpdate();

        conn.commit();

        System.out.println("Transfer successful");

    } catch (Exception e) {

        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        e.printStackTrace();

    } finally {

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
    public void checkBal(int accId){
        Account acc=findAcc(accId);
        if (acc!=null){
            System.out.println("balance :"+acc.getBalance());
        }else{
            System.out.println("account not found");
        }
    }
    public void showTransactions(int accId) {

    try (Connection conn = DBConnection.getConnection()) {

        String sql = "SELECT * FROM transactions WHERE acc_id = ? ORDER BY txn_id DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, accId);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        System.out.println("\n Transaction History ");

        while (rs.next()) {
            found = true;

            System.out.println(
                "ID: " + rs.getInt("txn_id") +
                " | Type: " + rs.getString("type") +
                " | Amount: " + rs.getDouble("amount") +
                " | Date: " + rs.getTimestamp("date")
            );
        }

        if (!found) {
            System.out.println("No transactions found");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public boolean login(String username, String password) {

    try (Connection conn = DBConnection.getConnection()) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        return rs.next();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
    

