package Models;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.File;

public class FormValidator {

    // Validate required text fields
    public static boolean validateRequiredField(TextField field, String fieldName, StringBuilder errorMessage) {
        field.setStyle("");  // Reset the style
        if (field.getText().trim().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            errorMessage.append(fieldName).append(" is required.\n");
            return false;
        }else {
            String input = field.getText().trim();
            if (!input.matches("^[a-zA-Z\\s]+$")) { // Allow only letters and spaces
                field.setStyle("-fx-border-color: red;");
                errorMessage.append(fieldName).append(" invalid.\n"); // Shortened
                return false;
            }
        }
        return true;
    }
    // Validate inputs matches in given pattern
    public static boolean validatePatternField(TextField field, String fieldName, String pattern, StringBuilder errorMessage) {
        field.setStyle("");  // Reset the style
        String input = field.getText();
        if (!input.matches(pattern)) {
            field.setStyle("-fx-border-color: red;");
            errorMessage.append(fieldName).append(" invalid.\n");
            return false;
        }
        return true;
    }
    // Validate numeric fields (integer)
    public static boolean validateNumericField(TextField field, String fieldName, StringBuilder errorMessage) {
        field.setStyle("");// Reset the style
        //System.out.println("Numeric Input"+field.getText());
        try {
            Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            field.setStyle("-fx-border-color: red;");
            errorMessage.append(fieldName).append(" must be a valid number.\n");
            return false;
        }
        return true;
    }

    // Validate DatePicker
    public static boolean validateDatePicker(DatePicker datePicker, String fieldName, StringBuilder errorMessage) {
        datePicker.setStyle("");  // Reset the style
        if (datePicker.getValue() == null) {
            datePicker.setStyle("-fx-border-color: red;");
            errorMessage.append(fieldName).append(" is required.\n");
            return false;
        }
        return true;
    }

    // Validate file (size and existence)
    public static boolean validateFile(File file, long maxSizeKB, StringBuilder errorMessage) {
        if (file == null || !file.exists()) {
            errorMessage.append("File is required and must exist.\n");
            return false;
        }
        if (file.length() > maxSizeKB * 1024) {
            errorMessage.append("File size must not exceed").append(maxSizeKB).append(" KB.\n");
            return false;
        }
        return true;
    }

    // Helper method to reset validation styles and messages
    public static void resetValidation(Label errorLabel, TextField... fields) {
        for (TextField field : fields) {
            field.setStyle("");
        }
        errorLabel.setText("");
        errorLabel.setStyle("");
    }

    // Method to set error messages
    public static void displayErrorMessages(Label errorLabel, StringBuilder errorMessage) {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText(errorMessage.toString());
    }

    // Method to display success message
    public static void displaySuccessMessage(Label errorLabel, String successMessage) {
        errorLabel.setTextFill(Color.GREEN);
        errorLabel.setText(successMessage);
    }
}
