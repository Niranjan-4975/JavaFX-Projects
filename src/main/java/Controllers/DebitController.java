package Controllers;

import Models.Model;
import Models.Operation;
import View.User;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DebitController implements Initializable {
    public TextField account_number;
    public Button search;
    public Label debit_amount;
    public TextField debit_text_box;
    public Button submit;
    public Button cancel;
    public Label acc_nameLabel;
    public Label Reg_noLabel;
    public Label acc_noLabel;
    public TextField show_accname;
    public TextField show_regNo;
    public TextField show_accNo;
    public Label err_Label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submit.setOnAction(_ -> onSubmit());
        cancel.setOnAction(_ -> onCancel());
        search.setOnAction(_ ->onSearch());
        setVisibility(false);
    }
    protected void setVisibility(boolean visible) {
        show_accNo.setVisible(visible);
        show_regNo.setVisible(visible);
        show_accname.setVisible(visible);
        //Label visibility
        acc_nameLabel.setVisible(visible);
        acc_noLabel.setVisible(visible);
        Reg_noLabel.setVisible(visible);
    }
    //Closing current Stage via any buttons
    private void closeStage() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        Model.getInstance().getViewfactory().closeStage(stage);
    }

    //OnSearch
    private void onSearch() {
        int accountNumber;
        err_Label.setText("");
        err_Label.setStyle("");
        //Validation for Input Value must Number, handling exception
        try {
            accountNumber = Integer.parseInt(account_number.getText());
        } catch (NumberFormatException ne) {
            // System.err.println("Invalid account number format: " + ne.getMessage());
            account_number.setStyle("-fx-border-color: red;");
            err_Label.setText("Invalid account number, it must be number");
            return; // Exit the method if the account number is invalid
        }
        if (accountNumber > 0) {
            //Searching User via acc no and Showing received data on screen
            User user;
            try {
                user = Model.getInstance().getDataBaseConnection().searchUser(accountNumber);
                if (user != null) {
                    // Processing the received data from DB
                    show_accname.setText(user.getName());
                    show_regNo.setText(String.valueOf(user.getRegNo()));
                    show_accNo.setText(String.valueOf(user.getAccNo()));
                    setVisibility(true);
                    account_number.setStyle("");
                    err_Label.setStyle("-fx-text-fill: green;");
                    err_Label.setText("User Found!!!");
                } else {
                    // System.out.println("No user found with account number: " + accountNumber);
                    err_Label.setText("No user found with account number: " + accountNumber);
                }
            } catch (SQLException se) {
                // System.err.println("Database error: " + se.getMessage());
                err_Label.setText("Database error: " + se.getMessage());
            }
        } else {
            err_Label.setText("");
            err_Label.setText("Account Number must be Non-zero Number to \n" + "Search Account Holder Details");
        }
        account_number.setText("");
    }

    //On Submit
    private void onSubmit() {
        if (validateForm(account_number, debit_text_box)) {
            int acc_no;
            double debt_amount;
            try {
                // Parse account number and credit amount
                acc_no = Integer.parseInt(account_number.getText());
                debt_amount = Double.parseDouble(debit_text_box.getText());
            } catch (NumberFormatException e) {
                // Handle NumberFormatException and show error message
                err_Label.setText("Invalid input.");
                return; // Exit the method if there's an exception
            }
            err_Label.setStyle("");
            //Create a background Task to perform the transaction
            Task<Pair<String,Double>> transactionTask = getDoubleTask(acc_no, debt_amount);
            new Thread(transactionTask).start();
        }
        account_number.setText("");
        debit_text_box.setText("");
    }
    //Separate Method to Perform Background Task
    private Task<Pair<String,Double>> getDoubleTask(int acc_no, double debt_amount) {
        Task<Pair<String ,Double>> transactionTask = new Task<>() {
            @Override
            protected Pair<String,Double> call() {
                // Perform the transaction in the background
                return Model.getInstance().getTransactionsProcessing().selectTransType(Operation.DEBIT, acc_no,0, debt_amount);
            }
        };
        transactionTask.setOnSucceeded(_ -> {
            Pair<String, Double> result = transactionTask.getValue();
            if (result.getKey().equals("Insufficient funds!!")||result.getKey().equals("Invalid debit amount")){
                err_Label.setText(result.getKey()+"\n" + "Current Balance: " + result.getValue());
            }else {
                err_Label.setStyle("-fx-text-fill: green;");
                err_Label.setText(result.getKey()+"\n" + "New Balance: " + result.getValue());
            }
        });
        transactionTask.setOnFailed(_ -> err_Label.setText("Transaction failed: " + transactionTask.getException().getMessage()));
        return transactionTask;
    }

    private boolean validateForm(TextField account_number, TextField debit_text_box) {
        account_number.setStyle("");
        debit_text_box.setStyle("");
        err_Label.setText("");
        boolean isValid = true;
        StringBuilder errmassage = new StringBuilder();
        if (account_number.getText().isEmpty()) {
            account_number.setStyle("-fx-border-color: red;");
            errmassage.append("Account No cannot be empty.\n");
        } else {
            try {
                int accno = Integer.parseInt(account_number.getText());
                if (accno <= 0) { // Validate for negative or zero
                    account_number.setStyle("-fx-border-color: red;");
                    errmassage.append("Account No must be a positive number.\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                account_number.setStyle("-fx-border-color: red;");
                errmassage.append("Account No must be a number.\n");
                isValid = false;
            }
        }
        if (debit_text_box.getText().isEmpty()) {
            debit_text_box.setStyle("-fx-border-color: red;");
            errmassage.append("Debit amount cannot be empty.\n");
            isValid = false;
        } else {
            try {
                double debit_am = Integer.parseInt(debit_text_box.getText());
                if (debit_am <= 0) { // Validate for negative or zero
                    debit_text_box.setStyle("-fx-border-color: red;");
                    errmassage.append("Debit amount must be a positive number.\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                debit_text_box.setStyle("-fx-border-color: red;");
                errmassage.append("Debit amount must be a number.\n");
                isValid = false;
            }
        }
        if (!isValid) {
            err_Label.setStyle("");
            err_Label.setText(errmassage.toString());
        }
        return isValid;
    }
    private void onCancel() {
        closeStage();
    }
}