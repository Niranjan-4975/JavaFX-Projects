package Controllers;

import Models.FormValidator;
import Models.Model;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BalanceController implements Initializable {
    public TextField account_number_text_box;
    public Button search_button;
    public Label total_balance_amount_label;
    public Label err_Label;
    public TextField show_Balfield;
    public Button close_Button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_button.setOnAction(_ -> executeBalSearch());
        close_Button.setOnAction(_ -> closeWindow());
    }

    private void closeWindow() {
        Stage stage = (Stage) close_Button.getScene().getWindow();
        Model.getInstance().getViewfactory().closeStage(stage);
    }

    // Method to perform the balance search
    private void executeBalSearch() {
        int accountNumber;
        StringBuilder message = new StringBuilder();
        String pattern = "\\d{4}";
        boolean isValid = true;
        FormValidator.resetValidation(err_Label, account_number_text_box, show_Balfield);
        if (isValid &= FormValidator.validateNumericField(account_number_text_box, "Account No", message)) {
            isValid = FormValidator.validatePatternField(account_number_text_box, "Account No", pattern, message);
        }
        if (isValid) {
            FormValidator.displaySuccessMessage(err_Label, "Validation Successful!!");
            try {
                accountNumber = Integer.parseInt(account_number_text_box.getText());
            } catch (NumberFormatException e) {
                //err_Label.setText("Invalid account number, it must be number");
                return;
            }
            // Create a background Task for balance search
            Task<Double> transactionTask = onBalSearch(accountNumber);
            new Thread(transactionTask).start();
            account_number_text_box.setText("");
        } else {
            FormValidator.displayErrorMessages(err_Label, message);
        }
        account_number_text_box.setText("");
    }

    private Task<Double> onBalSearch(int accountNumber) {
        Task<Double> transactionTask = new Task<>() {
            @Override
            protected Double call() {
                if (Model.getInstance().getDataBaseConnection().checkAccountExist(accountNumber)) {
                    // Perform the background task and return the current balance
                    return Model.getInstance().getTransactionsProcessing().getCurrentBalance(accountNumber);
                } else {
                    err_Label.setTextFill(Color.RED);
                    err_Label.setText("No User Found with Account number " + accountNumber);
                }
                return 0.0;
            }
        };
        transactionTask.setOnSucceeded(_ -> {
            Double balance = transactionTask.getValue();
            err_Label.setStyle("-fx-text-fill: green;");
            err_Label.setText("Your Account Current balance is : " + balance);
            show_Balfield.setText(String.valueOf(balance));
        });
        transactionTask.setOnFailed(_ -> {
            err_Label.setStyle("-fx-text-fill: red;");
            err_Label.setText("Something Went Wrong " + transactionTask.getException().getMessage());
        });
        return transactionTask;
    }

}
