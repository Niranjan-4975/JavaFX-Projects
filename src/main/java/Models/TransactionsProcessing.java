package Models;

import View.PairReturnMassaging;
import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TransactionsProcessing {
    private final Connection connection = Model.getInstance().getDataBaseConnection().getConnec();
    private final PairReturnMassaging result = new PairReturnMassaging();

    private void performTransaction(int accNo, Double credit, Double debit,Double newBalance){
        try {
            connection.setAutoCommit(false); // Start transaction
            // Insert into transaction table
            String insertTxnSql = "INSERT INTO transactions (account_no, debit, credit, txn_date, balance, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertTxnStmt = connection.prepareStatement(insertTxnSql)) {
                insertTxnStmt.setInt(1, accNo);
                // Using ternary operator to simplify null checks
                insertTxnStmt.setObject(2, debit, Types.DOUBLE);
                insertTxnStmt.setObject(3, credit, Types.DOUBLE);
                String timestamp = getTxndateAndTimestamp();
                String txnDate = getDateFromTimestamp(timestamp);
                insertTxnStmt.setString(4, txnDate);
                // Calculate new balance
                insertTxnStmt.setDouble(5, newBalance);
                insertTxnStmt.setString(6,timestamp);
                insertTxnStmt.executeUpdate();
            }

            // Update user_data table
            String updateBalanceSql = "UPDATE user_data SET total_balance = ? WHERE account_no = ?";
            try (PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceSql)) {
                updateBalanceStmt.setDouble(1,newBalance);
                updateBalanceStmt.setInt(2, accNo);
                updateBalanceStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction on error
            } catch (SQLException se) {
                se.getLocalizedMessage();
            }
            e.getLocalizedMessage(); // Handle exception
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit mode
                }catch (SQLException se){
                    se.getLocalizedMessage();
                }
            }
        }
    }

    private String getTxndateAndTimestamp() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return now.format(formatter);
    }
    public String getDateFromTimestamp(String txnDate) {
        int spaceIndex = txnDate.indexOf(" ");
        if (spaceIndex != -1)
            return txnDate.substring(0, spaceIndex);
        return txnDate;
    }

    public Pair<String, Double> selectTransType(Operation operation, int accNo, double credit, double debit) {
        double newBalance;
        double currentBal = getCurrentBalance(accNo);
        System.out.println("Current Balance"+currentBal);
        switch (operation) {
            case CREDIT:
                if (credit > 0) {
                    newBalance = currentBal + credit;
                    performTransaction(accNo,credit,debit,newBalance);
                    System.out.println("Credit Operation");// Only add credit when it's provided
                } else {
                    System.out.println("Invalid credit amount");
                    result.massage = "Invalid credit amount";
                    result.value = currentBal;
                    return result.myMethod(); // Return current balance if credit is invalid
                }
                break;
            case DEBIT:
                if (debit > 0 && currentBal >= debit) {
                    newBalance = currentBal - debit;
                    performTransaction(accNo,credit,debit,newBalance);// Only subtract if the balance is enough
                    System.out.println("Debit Operation");
                } else if (currentBal < debit) {
                    System.out.println("Insufficient funds");
                    result.massage = "Insufficient funds";
                    result.value = currentBal;
                    return result.myMethod();  // Return current balance if insufficient funds
                } else {
                    System.out.println("Invalid debit amount");
                    result.massage = "Invalid debit amount";
                    result.value = currentBal;
                    return result.myMethod();  // Return current balance if debit is invalid
                }
                break;
            default:
                System.out.println("Operation Failed");
                result.massage = "Operation Failed";
                return result.myMethod(); // Return the current balance if operation is invalid
        }
        result.massage = "Transaction Successful!!!";
        result.value = newBalance;
        return result.myMethod();
    }

    public double getCurrentBalance(int accNo){
        System.out.println("Inside getCurrent Balance");
        String sql = "SELECT total_balance FROM user_data WHERE account_no = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_balance");
            }
        }catch (SQLException se){
            se.getLocalizedMessage();
        }
        return 0;
    }
}
