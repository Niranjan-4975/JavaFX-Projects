package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {

    private Stage currentStage;

    public ViewFactory() {
    }

    //Creating stage
    private void createStage(FXMLLoader fxmlLoader) {
        if (currentStage != null) {
            currentStage.close();
        }
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (NullPointerException | IOException e) {
            e.getLocalizedMessage();
        }
        Stage stage = new Stage();
        stage.setTitle("MaziBachatBank");
        stage.setResizable(false);
        stage.setScene(scene);
        currentStage = stage;
        stage.show();
    }

    //Closing Stage via Cancel button
    public void closeStage(Stage stage) {
        stage.close();
    }

    // Credit Window Loading
    public void showCreditWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Credit.fxml"));
        createStage(fxmlLoader);
    }

    // Debit Window Loading
    public void showDebitWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Debit.fxml"));
        createStage(fxmlLoader);
    }

    //Passbook Window Loading
    public void showPassbookWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Passbook.fxml"));
        createStage(fxmlLoader);
    }

    //Add Account Window Loading
    public void showAddAccountWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/AddAccount.fxml"));
        createStage(fxmlLoader);
    }

    //Close Account Window Loading
    public void showCloseAccountWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/CloseAccount.fxml"));
        createStage(fxmlLoader);
    }

    //Balance Window Loading
    public void showBalanceWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Balance.fxml"));
        createStage(fxmlLoader);
    }
}
