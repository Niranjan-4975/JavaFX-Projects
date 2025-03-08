package Controllers;

import Models.Model;
import View.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class MainScrController implements Initializable {
    public Button credit_btn;
    public Button debit_btn;
    public Button passbook_btn;
    public Button balance_btn;
    public Button addacc_btn;
    public Button closeacc_btn;
    @FXML
    private Label show_TotalBal;
    @FXML
    private ImageView show_Image;
    @FXML
    private Label show_Name;
    @FXML
    private Label show_AccNo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        credit_btn.setOnAction(_ -> onCredit());
        debit_btn.setOnAction(_ -> onDebit());
        passbook_btn.setOnAction(_ -> onPassbook());
        balance_btn.setOnAction(_ -> onBalance());
        addacc_btn.setOnAction(_ -> onAddAccount());
        closeacc_btn.setOnAction(_ -> onCloseAccount());
        displayDetails();
        displayTotalBal();
    }
    //Showing totalBank Balance
    private void displayTotalBal(){
        //System.out.println("In Display TotalBal");
        Task<Double> task = new Task<>() {
            @Override
            protected Double call() {
                //System.out.println("Inside Task TotalBal");
                return Model.getInstance().getDataBaseConnection().fetchTotalBankBalance();
            }
        };
        task.setOnSucceeded(_ ->{
            double totalBal = task.getValue();
            //System.out.println("Print TotalBal after Task" +totalBal);
            Platform.runLater(() -> show_TotalBal.setText("Remaining Balance of Bank :- " + totalBal));
        });
        new Thread(task).start();
    }

    //Showing user details on screen having max balance
    private void displayDetails() {
        System.out.println("In Display User");
        Task<User> task = new Task<>() {
            @Override
            protected User call() {
                return Model.getInstance().getDataBaseConnection().fetchMaxbalanceUser();
            }
        };
        task.setOnSucceeded(_ -> {
            try {
                User user = task.get();
                Platform.runLater(() -> {
                    try {
                        show_Name.setText("Name: " + user.getName());
                        show_AccNo.setText("Account No: " + user.getAccNo());
                        // Convert Blob to Image
                        InputStream inputStream = new ByteArrayInputStream(user.getUserImage());
                        Image userImage = new Image(inputStream);
                        show_Image.setFitWidth(200);
                        show_Image.setFitHeight(200);
                        show_Image.setSmooth(true);
                        show_Image.setImage(userImage);
                    } catch (NullPointerException e) {
                        e.getLocalizedMessage();
                    }
                });
            } catch (NullPointerException | InterruptedException | ExecutionException e) {
                e.getLocalizedMessage();
            }
        });
        task.setOnFailed(_ ->show_Name.setText("Error"));
        new Thread(task).start();
    }

    // Calling Credit Window
    private void onCredit() {
        Model.getInstance().getViewfactory().showCreditWindow();
    }

    // Calling Debit Window
    private void onDebit() {
        Model.getInstance().getViewfactory().showDebitWindow();
    }

    // Calling Passbook Window
    private void onPassbook() {
        Model.getInstance().getViewfactory().showPassbookWindow();
    }

    // Calling AddAccount Window
    private void onAddAccount() {
        Model.getInstance().getViewfactory().showAddAccountWindow();
    }

    // Calling CloseAccount Window
    private void onCloseAccount() {
        Model.getInstance().getViewfactory().showCloseAccountWindow();
    }

    // Calling Balance Window
    private void onBalance() {
        Model.getInstance().getViewfactory().showBalanceWindow();
    }
}