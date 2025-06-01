package main;

import Models.Model;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MaziBachatBankApplication extends Application {

    // Starting Application and Opening MainScreen Window
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/mainscr.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("MaziBachatBank");
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/bank_PNG21.png"))));
        primaryStage.setScene(scene);
        primaryStage.show();
        Model.getInstance().getDataBaseConnection().tableCreation();

        //For close Application Completely
        primaryStage.setOnCloseRequest(_ -> Model.getInstance().getDataBaseConnection().closeConnection());
        primaryStage.setOnCloseRequest(_ -> Platform.exit());
    }

    //Main Method
    public static void main(String[] ignoredArgs) {
        try {
            launch();
        } catch (NumberFormatException e) {
                e.getLocalizedMessage();
        }
    }
}