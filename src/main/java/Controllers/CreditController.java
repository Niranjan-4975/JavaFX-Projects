package Controllers;

import Models.FormValidator;
import Models.Model;
import Models.Operation;
import View.User;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.util.Pair;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreditController implements Initializable {
    public TextField account_number;
    public TextField credit_text_box;
    public Button search_button;
    public Button submit_button;
    public Button cancel_button;
    public Label acc_nameLabel;
    public Label reg_noLabel;
    public Label acc_noLabel;
    public TextField show_name;
    public TextField show_regNo;
    public TextField show_accNo;
    public Label err_label;
    private int accno;
    double creditamount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancel_button.setOnAction(_ -> onCancel());
        search_button.setOnAction(_ -> onSearch());
        submit_button.setOnAction(_ -> validateCrForm());
        setVisibility(false);
    }
    protected void setVisibility(boolean visible) {
        show_accNo.setVisible(visible);
        show_regNo.setVisible(visible);
        show_name.setVisible(visible);
        //Label visibility
        acc_nameLabel.setVisible(visible);
        acc_noLabel.setVisible(visible);
        reg_noLabel.setVisible(visible);
    }
    //Closing current Stage via any buttons
    private void closeStage() {
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        Model.getInstance().getViewfactory().closeStage(stage);
    }
    //OnSearch
    private void onSearch() {
        int accountNumber;
        err_label.setText("");
        //Validation for Input Value must Number, handling exception
        try {
            accountNumber = Integer.parseInt(account_number.getText());
        } catch (NumberFormatException ne) {
            // System.err.println("Invalid account number format: " + ne.getMessage());
            account_number.setStyle("-fx-border-color: red;");
            err_label.setStyle("");
            err_label.setText("Invalid account number, it must be number");
            return; // Exit the method if the account number is invalid
        }
        if (accountNumber > 0) {
            //Searching User via acc no and Showing received data on screen
            User user;
            try {
                boolean exist = Model.getInstance().getDataBaseConnection().checkAccountExist(accountNumber);
                if (exist) {
                    user = Model.getInstance().getDataBaseConnection().searchUser(accountNumber);
                    if (user != null) {
                        // Processing the received data from DB
                        show_name.setText(user.getName());
                        show_regNo.setText(String.valueOf(user.getRegNo()));
                        show_accNo.setText(String.valueOf(user.getAccNo()));
                        setVisibility(true);
                        account_number.setStyle("");
                        err_label.setTextFill(Color.GREEN);
                        err_label.setText("User Found!!!");
                    }
                } else {
                    // System.out.println("No user found with account number: " + accountNumber);
                    err_label.setTextFill(Color.RED);
                    err_label.setText("No user found with account number: " + accountNumber);
                }
            } catch (SQLException se) {
                // System.err.println("Database error: " + se.getMessage());
                err_label.setText("Database error: " + se.getMessage());
            }
        } else {
            err_label.setText("");
            err_label.setText("Account Number must be Non-zero Number to \n" + "Search Account Holder Details");
        }
        account_number.setText("");
    }
    // Validate form inputs
    public void validateCrForm() {
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;
        // Reset validation styles and clear error label
        FormValidator.resetValidation(err_label, account_number, credit_text_box);
        // Validate numeric fields
        isValid &= FormValidator.validateNumericField(credit_text_box, "Credit Amount", errorMessage);
        if (isValid &= FormValidator.validateNumericField(account_number, "Account No", errorMessage)) {
            // Define the pattern you want for the account number (for example, only digits)
            String accountNumberPattern = "\\d{4}";  // 10-digit number// Validate account number field using the pattern
            isValid = FormValidator.validatePatternField(account_number, "Account No", accountNumberPattern, errorMessage);
        }
        try {
            accno = Integer.parseInt(account_number.getText());
            try {
                creditamount = Integer.parseInt(credit_text_box.getText());
            } catch (NumberFormatException ne) {
                ne.getLocalizedMessage();
            }
        } catch (NumberFormatException ne) {
            ne.getLocalizedMessage();
        }
        // Display messages based on validation
        if (isValid) {
            FormValidator.displaySuccessMessage(err_label, "Validation successful!! Proceeding...");
            submitCredit(accno, creditamount);// Method to submit the form
            account_number.setText("");
            credit_text_box.setText("");
            //System.out.println("Reset Styles");
        } else {
            FormValidator.displayErrorMessages(err_label, errorMessage);
        }
    }
    //submit to Credit Operation
    private void submitCredit(int accno, double creditamount) {
        //Create a background Task to perform the transaction
        boolean exist = Model.getInstance().getDataBaseConnection().checkAccountExist(accno);
        if (exist) {
            Task<Pair<String, Double>> transactionTask = getCreditOperation(accno, creditamount);
            new Thread(transactionTask).start();
        }else{
            //System.out.println("User Not Found");
            err_label.setStyle("");
            err_label.setText("");
            err_label.setTextFill(Color.RED);
            err_label.setText("User Not Exist!!");
        }
    }
    //Separate Method to Perform Background Task
    private Task<Pair<String, Double>> getCreditOperation(int acc_no, double cr_amount) {
        Task<Pair<String, Double>> transactionTask = new Task<>() {
            @Override
            protected Pair<String, Double> call() {
                // Perform the transaction in the background
                return Model.getInstance().getTransactionsProcessing().selectTransType(Operation.CREDIT, acc_no, cr_amount, 0);
            }
        };
        transactionTask.setOnSucceeded(_ -> {
            Pair<String, Double> result = transactionTask.getValue();
            err_label.setStyle("-fx-text-fill: green;");
            err_label.setText(result.getKey() + "\n" + "New Balance: " + result.getValue());
        });
        transactionTask.setOnFailed(_ -> err_label.setText("Transaction failed: " + transactionTask.getException().getMessage()));
        return transactionTask;
    }
    //on Cancel
    private void onCancel() {
        closeStage();
    }
}
