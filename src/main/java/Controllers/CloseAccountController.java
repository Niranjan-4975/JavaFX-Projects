package Controllers;

import Models.Model;
import View.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CloseAccountController implements Initializable {
    @FXML
    public Label acc_holderLabel;
    @FXML
    public Label reg_noLabel;
    @FXML
    public Label acc_noLabel;
    @FXML
    private TextArea show_name;
    @FXML
    private TextArea show_regNo;
    @FXML
    private TextArea show_accNo;
    @FXML
    private Button closeaccount_button;
    public Label err_label;
    @FXML
    private TextField account_number;
    @FXML
    private Button search_button;
    @FXML
    private Button cancel_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancel_button.setOnAction(_ ->onCancel());
        closeaccount_button.setOnAction(_ ->onCloseAcc());
        search_button.setOnAction(_ ->onSearch());
        setVisibility(false);
    }
    //On Key pressed for Input
    @FXML
    private void onInputStart() {
        account_number.setText("");
        account_number.setStyle("");
    }

    private void setVisibility(boolean visible) {
        show_accNo.setVisible(visible);
        show_regNo.setVisible(visible);
        show_name.setVisible(visible);

        acc_holderLabel.setVisible(visible);
        reg_noLabel.setVisible(visible);
        acc_noLabel.setVisible(visible);
    }
    //Closing Current Stage via Any Buttons
    private void closeCurrentStage(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        Model.getInstance().getViewfactory().closeStage(stage);
    }
    //onClick Search
    private void onSearch() {
        int accountNumber;
        err_label.setText("");
        //Validation for Input Value must Number, handling exception
        try {
            accountNumber = Integer.parseInt(account_number.getText());
        } catch (NumberFormatException ne) {
            // System.err.println("Invalid account number format: " + ne.getMessage());
            account_number.setStyle("-fx-border-color: red;");
            err_label.setText("Invalid account number, it must be number");
            return; // Exit the method if the account number is invalid
        }
        if (accountNumber > 0) {
            User user;
            try {
                user = Model.getInstance().getDataBaseConnection().searchUser(accountNumber);
                if (user != null) {
                    show_name.setText(user.getName());
                    show_regNo.setText(String.valueOf(user.getRegNo()));
                    show_accNo.setText(String.valueOf(user.getAccNo()));

                    setVisibility(true);
                } else {
                    // System.out.println("No user found with account number: " + accountNumber);
                    err_label.setText("No user found with account number: " + accountNumber);
                }
            } catch (SQLException se) {
                // System.err.println("Database error: " + se.getMessage());
                err_label.setText("Database error: " + se.getMessage());
                se.getLocalizedMessage();
            }
        }else {
            err_label.setText("");
            err_label.setText("Account Number must be Non-zero Number to \n"+"Search Account Holder Details");
        }
        account_number.setText("");
    }
    //On click CloseAcc
    private void onCloseAcc(){
        err_label.setText("");
        int acc_no;
        try {
            acc_no = Integer.parseInt(account_number.getText());
        }catch (NumberFormatException ne){
            System.err.println(ne.getMessage());
            account_number.setStyle("-fx-border-color: red;");
            err_label.setText("");
            err_label.setText("Account Number must be Number to Close Account");
            return;
        }
        if (acc_no > 0) {
            boolean exist =Model.getInstance().getDataBaseConnection().checkAccountExist(acc_no);
            if (exist) {
                Model.getInstance().getDataBaseConnection().deleteUser(acc_no);
                err_label.setTextFill(Color.GREEN);
                err_label.setText("Account Closed Successfully and deleted all details\n"+"associated with it");
            }else {
                err_label.setText("User not exist.");
            }
        }else {
            err_label.setText("");
            err_label.setText("Account Number Must be Non-zero Number");
        }
        account_number.setText("");
    }
    //On click Cancel
    private void onCancel() {closeCurrentStage();}

}
