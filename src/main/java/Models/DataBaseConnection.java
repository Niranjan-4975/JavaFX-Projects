package Models;

import Controllers.AddAccountController;
import View.PassbookTransactions;
import View.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class DataBaseConnection {
    private Connection connec;
    private static final String CREATE_USER_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS user_data (
                          account_no INTEGER PRIMARY KEY NOT NULL UNIQUE,
                          name TEXT NOT NULL,
                          father_name TEXT NOT NULL,
                          last_name TEXT NOT NULL,
                          mother_name TEXT NOT NULL,
                          date_of_birth TEXT NOT NULL,
                          registration_no INTEGER NOT NULL,
                          total_balance REAL,
                          user_image BLOB NOT NULL,
                          created_at TEXT DEFAULT (datetime('now'))
                      );
            """;
    private static final String CREATE_TRANSACTION_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS transactions(
                        sr_no INTEGER PRIMARY KEY AUTOINCREMENT,
                        account_no INTEGER NOT NULL,
                        debit REAL,
                        credit REAL,
                        txn_date TEXT,
                        balance REAL,
                        created_at TEXT DEFAULT (datetime('now')),
                        FOREIGN KEY (account_no) REFERENCES user_data(account_no)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE
                     );
            """;

    public DataBaseConnection() {
        try {
            this.connec = DriverManager.getConnection("jdbc:sqlite:StudentBank.db");
            System.out.println("Connection Successful!!");
            try (Statement stmt = connec.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    // Get Connection, checking if it's closed or not
    public synchronized Connection getConnec() {
        try {
            if (connec == null || connec.isClosed()) {
                connec = DriverManager.getConnection("jdbc:sqlite:StudentBank.db");
                System.out.println("Re-established connection.");
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
        }
        return connec;
    }

    //Close connection on exit of application
    public void closeConnection() {
        if (connec != null) {
            try {
                connec.close();
                System.out.println("Connection Close Successfully");
            } catch (SQLException e) {
                e.getLocalizedMessage();
            }
        }
    }

    //For table creation, at app startup
    public void tableCreation() {
        try (Statement stmt = getConnec().createStatement()){
            connec.setAutoCommit(false); // Start a transaction
            // Execute multiple table creation statements
            stmt.executeUpdate(CREATE_USER_TABLE_SQL);
            stmt.executeUpdate(CREATE_TRANSACTION_TABLE_SQL);
            connec.commit(); // Commit the transaction if all statements succeed
            System.out.println("Tables created successfully or already exist.");
        } catch (SQLException e) {
            e.getLocalizedMessage();
            try {
                if (connec != null) {
                    connec.rollback(); // Rollback the transaction if any statement fails
                    System.out.println("Transaction rolled back due to an error.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.getLocalizedMessage();
            }
        } finally {
            try {
                if (connec != null) {
                    connec.setAutoCommit(true);
                    //System.out.println("Auto Commit true");// Restore auto-commit mode
                }
            } catch (SQLException e) {
                e.getLocalizedMessage();
            }
        }
    }

    //Add Account Button Function, Collecting User Data and Saving into DB
    public void saveUser(String username, String fathername, String lastname, String mothername, String dob, int regnumber, int accountnumber, File userimage, String timestamp) {
        String query = "INSERT INTO user_data(account_no,name,father_name,last_name,mother_name,date_of_birth,registration_no,user_image,created_at) values(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = getConnec().prepareStatement(query)) {
            statement.setInt(1, accountnumber);
            statement.setString(2, username);
            statement.setString(3, fathername);
            statement.setString(4, lastname);
            statement.setString(5, mothername);
            statement.setString(6, dob);
            statement.setInt(7, regnumber);
            byte[] image = AddAccountController.getImageBytes(userimage);
            statement.setBytes(8, image);
            statement.setString(9, timestamp);
            statement.executeUpdate();
            System.out.println("Details Saved in DB");
        } catch (SQLException | IOException e) {
            e.getLocalizedMessage();
        }
    }

    //Delete Account Button Function, Deleting User Data from DB
    public void deleteUser(int accNo) {
        String query = "DELETE FROM user_data WHERE account_no = ?";
        try (PreparedStatement statement = getConnec().prepareStatement(query)) {
            statement.setInt(1, accNo);
            int rowsaffected = statement.executeUpdate();
            if (rowsaffected > 0) {
                System.out.println("User with " + accNo + " deleted successfully");
            }
        } catch (SQLException se) {
            se.getLocalizedMessage();
        }
    }

    //Search User Button Function, Searching User in DB
    public User searchUser(int accountNumber) throws SQLException {
        String query = "SELECT name, registration_no, account_no FROM user_data WHERE account_no = ?";
        try (PreparedStatement stmt = getConnec().prepareStatement(query)) {
            stmt.setInt(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String name = rs.getString("name");
                    int regNo = rs.getInt("registration_no");
                    int accNo = rs.getInt("account_no");
                    return new User(accNo,regNo,name,null);
                }
            }
        }catch (SQLException se ){
            se.getLocalizedMessage();
        }
        return null;
    }

    //Check if the account number exists
    public boolean checkAccountExist(int accno) {
        String query = "SELECT COUNT(*) FROM user_data WHERE account_no = ?";
        try (PreparedStatement statement = getConnec().prepareStatement(query)){
            statement.setInt(1, accno);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException se) {
            se.getLocalizedMessage();
        }
        return false;
    }

    //Method to fetch transaction data from the database
    public ObservableList<PassbookTransactions> fetchTransactions(int offset, int limit) {
        //System.out.println("Fetch Transaction");
        ObservableList<PassbookTransactions> transactions = FXCollections.observableArrayList();
        //SQL query to fetch
        String query = "SELECT sr_no, account_no, debit, credit, txn_date, balance FROM transactions LIMIT "+limit+" OFFSET "+offset;
        //Fetch data now
        try (Statement stmt = getConnec().createStatement()){
            try (ResultSet rs = stmt.executeQuery(query)){
                while (rs.next()) {
                    int srNo = rs.getInt("sr_no");
                    int accNO = rs.getInt("account_no");
                    double debitamount = rs.getDouble("debit");
                    double creditamount = rs.getDouble("credit");
                    String txndate = rs.getString("txn_date");
                    double balance = rs.getDouble("balance");

                    transactions.add(new PassbookTransactions(srNo, accNO, creditamount, debitamount, txndate, balance));
                }
            }
        } catch (SQLException sq) {
            sq.getLocalizedMessage();
        }
        //System.out.println(transactions);
        return transactions;
    }

    //Method to get the total count of transaction records
    public int getTotalTransactionCount() {
        //System.out.println("get Total Txn");
        int totalRecords = 0;
        String query = "SELECT COUNT(*) FROM transactions";
        try (Statement stmt = getConnec().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                totalRecords = rs.getInt(1);
            }
        } catch (SQLException sq) {
            sq.getLocalizedMessage();
        }
        return totalRecords;
    }

    //fetch user having max balance
    public User fetchMaxbalanceUser() {
        String userQuery = """
                SELECT u.account_no ,name ,user_image
                FROM user_data u
                JOIN (
                    SELECT account_no , COUNT(*) AS transaction_count
                    FROM transactions
                    GROUP BY account_no
                ) t ON u.account_no = t.account_no
                WHERE u.total_balance = (SELECT MAX(total_balance) FROM user_data)
                ORDER BY t.transaction_count DESC
                LIMIT 1;
                """;
        try (Statement stmt = getConnec().createStatement();
             ResultSet rs = stmt.executeQuery(userQuery)){
            if (rs.next()){
                int accNo = rs.getInt("account_no");
                String name = rs.getString("name");
                byte[] userImage = rs.getBytes("user_image");
                return new User(accNo,0,name,userImage);
            }
        } catch (SQLException se) {
            se.getLocalizedMessage();
        }
        return null;
    }

    //Method get Total Balance of Bank, Balance = sum of balance of all users
    public double fetchTotalBankBalance() {
        System.out.println("Inside DB TotalBal");
        String query = "SELECT SUM(total_balance) AS total_bank_balance FROM user_data";
        double totalBankBal = 0;
        try (Statement stmt = getConnec().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                totalBankBal = rs.getDouble("total_bank_balance");
                System.out.println("TotalBal"+totalBankBal);
            }
        } catch (SQLException se) {
            se.getLocalizedMessage();
        }
        return totalBankBal;
    }
}
