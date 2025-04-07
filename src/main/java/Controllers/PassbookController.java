package Controllers;

import Models.Model;
import View.PassbookTransactions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PassbookController implements Initializable {

    @FXML
    private Button export_Excel_button;
    @FXML
    private TableView<PassbookTransactions> passbook_table;
    @FXML
    private TableColumn<PassbookTransactions, Integer> sr_No;
    @FXML
    private TableColumn<PassbookTransactions, Integer> account_No;
    @FXML
    private TableColumn<PassbookTransactions, Double> withdrawal_Amount;
    @FXML
    private TableColumn<PassbookTransactions, Double> deposit_Amount;
    @FXML
    private TableColumn<PassbookTransactions, String> txn_Date;
    @FXML
    private TableColumn<PassbookTransactions, Double> total_Balance;

    private final ObservableList<PassbookTransactions> transactionsList = FXCollections.observableArrayList();
    private boolean isMouseOverPassbook = false;
    private int currentOffset = 0;
    private final int RECORDS_PER_LOAD = 20;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        passbook_table.setOnMouseEntered(_ -> isMouseOverPassbook = true);
        passbook_table.setOnMouseExited(_ -> isMouseOverPassbook = false);
        passbook_table.setOnScroll(this::handleScrollEvent);
        export_Excel_button.setOnMouseClicked(_-> exportToExcel());
        loadInitialTransactions();
    }

    private void setupTable() {
        System.out.println("Setup Table");
        sr_No.setCellValueFactory(new PropertyValueFactory<>("srno"));
        account_No.setCellValueFactory(new PropertyValueFactory<>("accno"));
        withdrawal_Amount.setCellValueFactory(new PropertyValueFactory<>("debit"));
        deposit_Amount.setCellValueFactory(new PropertyValueFactory<>("credit"));
        txn_Date.setCellValueFactory(new PropertyValueFactory<>("txndate"));
        total_Balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        passbook_table.setItems(transactionsList);
    }

    private void handleScrollEvent(ScrollEvent event) {
        if (isMouseOverPassbook) {
            double deltaY = event.getDeltaY();
            if (deltaY > 0) {
                scrollUp();
            } else {
                scrollDown();
            }
        } else {
            event.consume();
        }
    }

    private boolean isAllRecordsLoaded = false;

    private void scrollUp() {
        if (isAllRecordsLoaded || currentOffset == 0) {
            return;
        }
        System.out.println("scroll up");
        if (currentOffset > 0) {
            currentOffset -= RECORDS_PER_LOAD;
            loadTransactions(currentOffset);
        }
    }

    private void scrollDown() {
        if (isAllRecordsLoaded) {
            return;
        }
        System.out.println("scroll down");
        int totalAvailableRecords = Model.getInstance().getDataBaseConnection().getTotalTransactionCount();
        // Prevent loading more if no more records are available
        if (currentOffset < totalAvailableRecords) {
            ObservableList<PassbookTransactions> newTransactions = Model.getInstance().getDataBaseConnection().fetchTransactions(currentOffset, RECORDS_PER_LOAD);
            // Only add if there are new records
            if (!newTransactions.isEmpty()) {
                for (PassbookTransactions record : newTransactions) {
                    if (!transactionsList.contains(record)) {
                        transactionsList.add(record);
                    }
                }
                currentOffset += RECORDS_PER_LOAD;  // Increment offset only if new records were added
            }
        }
        if (currentOffset >= totalAvailableRecords) {
            isAllRecordsLoaded = true;
        }
    }


    private void loadInitialTransactions() {
        System.out.println("Load Initial TXN");
        loadTransactions(currentOffset);//Load first 10 records
    }

    private void loadTransactions(int offset) {
        //System.out.println("load Txn");
        // Check if the offset is valid
        if (offset < 0 || offset >= Model.getInstance().getDataBaseConnection().getTotalTransactionCount()) {
            return; // Avoid loading if the offset is out of bounds
        }
        // Fetch transactions with respect to the offset and limit
        ObservableList<PassbookTransactions> newTransactions = Model.getInstance().getDataBaseConnection().fetchTransactions(offset, RECORDS_PER_LOAD);
        // Check if we have new transactions to add
        if (!newTransactions.isEmpty()) {
            // Avoid duplicates by ensuring that we're not adding already loaded transactions
            for (PassbookTransactions record : newTransactions) {
                if (!transactionsList.contains(record)) {
                    transactionsList.add(record);
                }
            }
        }
    }

    @FXML
    private void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Passbook Transactions");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Sr No", "Account No", "Withdrawal Amount", "Deposit Amount", "Transaction Date", "Total Balance"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        for (int i = 0; i < transactionsList.size(); i++) {
            PassbookTransactions transaction = transactionsList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(transaction.getSrno());
            row.createCell(1).setCellValue(transaction.getAccno());
            row.createCell(2).setCellValue(transaction.getDebit());
            row.createCell(3).setCellValue(transaction.getCredit());
            row.createCell(4).setCellValue(transaction.getTxndate());
            row.createCell(5).setCellValue(transaction.getBalance());
        }

        // Show save file dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("PassbookTransactions.xlsx");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                workbook.close();
            } catch (IOException e) {
                e.getLocalizedMessage();
            }
        }
    }
}

