package Controllers;

import Models.FormValidator;
import Models.Model;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddAccountController implements Initializable {
    public static int regno;
    public static int accno;
    public TextField lastname_field;
    public Label err_label;
    public TextField mothername_field;
    public TextField fathername_field;
    public TextField username_field;
    public TextField registration_field;
    public TextField accountno_field;
    public DatePicker userdob_field;
    public Button photo_picker;
    public Button submit_button;
    public Button cancel_button;
    public File user_image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submit_button.setOnAction(_ -> validateForm());
        cancel_button.setOnAction(_ -> onCancel());
        photo_picker.setOnAction(_ -> handleUploadPhoto());
    }
    // Delay closing the window by 10 seconds
    Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(5), // Delay of 5 seconds
            _ -> {
                // Close the window after 5 seconds
                closeCurrentStage();
            }
    ));
    private void onCancel() {
        closeCurrentStage();
    }
    //Handling Image Photo File Operations
    @FXML
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handleUploadPhoto() {
        if (user_image != null) {
            user_image.delete();
        }
        //Create file chooser
        FileChooser fileChooser = new FileChooser();
        //Set title for dialog
        fileChooser.setTitle("Select an image");
        //Set the file extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        //Open dialog box
        Stage stage = new Stage();
        File selectedfile = fileChooser.showOpenDialog(stage);
        if (selectedfile != null) {
            //upload the file
            user_image = selectedfile;
            System.out.println(selectedfile.getAbsolutePath());
        } else {
            System.out.println("File selection cancelled");
        }
        stage.close();
    }
    // Validate form inputs
    public void validateForm() {
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;
        // Reset validation styles and clear error label
        FormValidator.resetValidation(err_label, username_field, fathername_field, lastname_field, mothername_field, registration_field, accountno_field);
        // Validate text fields
        isValid &= FormValidator.validateRequiredField(username_field, "Username", errorMessage);
        isValid &= FormValidator.validateRequiredField(fathername_field, "Father's Name", errorMessage);
        isValid &= FormValidator.validateRequiredField(lastname_field, "Last Name", errorMessage);
        isValid &= FormValidator.validateRequiredField(mothername_field, "Mother's Name", errorMessage);
        // Validate DatePicker
        isValid &= FormValidator.validateDatePicker(userdob_field, "Date of Birth", errorMessage);
        // Validate numeric fields
        isValid &= FormValidator.validateNumericField(registration_field, "Registration No", errorMessage);
        if (isValid &= FormValidator.validateNumericField(accountno_field, "Account No", errorMessage)) {
            // Define the pattern you want for the account number (for example, only digits)
            String accountNumberPattern = "\\d{4}";  // 10-digit number// Validate account number field using the pattern
            isValid = FormValidator.validatePatternField(accountno_field, "Account No", accountNumberPattern, errorMessage);
        }
        // Validate file
        isValid &= FormValidator.validateFile(user_image, 100, errorMessage); // Max 100 KB
        try {
            regno = Integer.parseInt(registration_field.getText());
            try {
                accno = Integer.parseInt(accountno_field.getText());
            } catch (NumberFormatException ne) {
                ne.getLocalizedMessage();
            }
        } catch (NumberFormatException ne) {
            ne.getLocalizedMessage();
        }
        // Display messages based on validation
        if (isValid) {
            FormValidator.displaySuccessMessage(err_label, "Validation successful!! Proceeding...to Save User");
            String username = username_field.getText();
            String fathername = fathername_field.getText();
            String lastname = lastname_field.getText();
            String mothername = mothername_field.getText();
            String dob = userdob_field.getValue().toString();
            String timestamp = getCurrentTimestamp();
            submitFrom(username, fathername, lastname, mothername, dob, regno, accno, user_image, timestamp);  // Method to submit the form
        } else {
            FormValidator.displayErrorMessages(err_label, errorMessage);
        }
    }
    //Submits to DB to saved
    private void submitFrom(String username, String fathername, String lastname, String mothername, String finalDateOfBirth, int regno, int accno, File userImage, String timestamp) {
        new Thread(() -> {
            Model.getInstance().getDataBaseConnection().saveUser(username, fathername, lastname, mothername, finalDateOfBirth, regno, accno, userImage, timestamp);
            Platform.runLater(() -> {
                System.out.println(accno + "\n" + username + "\n" + fathername + "\n" + lastname + "\n" + mothername + "\n" + finalDateOfBirth + "\n" + regno + "\n" + userImage + "\n" + timestamp);
                timeline.setCycleCount(1);// Run once
                timeline.play(); // Start the timer
            });
        }).start();
    }
    //getCurrent Date Time
    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault()); // Get local date-time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return now.format(formatter);
    }
    //Converting image into Byte[] to store into DB, further processing
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] getImageBytes(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            is.read(buffer);
            return buffer;
        }
    }
    //Closing current Stage via any buttons
    private void closeCurrentStage() {
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        Model.getInstance().getViewfactory().closeStage(stage);
    }
}
