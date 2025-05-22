/***********************************************************************************************************************
 * Guest Info Form Controller: Manages the guest personal information like name, contact info, and a vin number. Has
 * dynamic form validation for all fields within the forms and final validation upon submission of each form.
 **********************************************************************************************************************/
package controllers;

import models.Guest;
import models.Reservation;
import utils.Logging;
import utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static app.Main.*;

public class GuestInfoFormController implements Initializable {
    @FXML private TextField nameTF, emailTF, phoneNumTF, addressTF, vinNumTF;

    private final String alertCssPath = "/ca/senecacollege/cpa/app/styles/alert-styles.css";
    private String originPoint;
    private Reservation reservation;
    private static int id = 0;

    // Initializable Method --------------------------------------------------------------------------------------------
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFieldToLettersOnly(nameTF);
        setTextFieldToNumsOnly(phoneNumTF);
        setVINFieldToLimitedVals(vinNumTF);
        readGuestIdValue();
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    public void confirmGuestInfo(ActionEvent event) {
        if (formEmptyChecker()) {
            showErrorAlert("Please fill all required fields of the form to continue");
            return;
        } else if (!validEmail()) {
            showErrorAlert("Please enter a valid email, something@mailing.com");
            return;
        } else if (!validAddress()) {
            showErrorAlert("Please enter a valid address, 142 something Dr. or similar");
            return;
        }
        String name = nameTF.getText();
        String email = emailTF.getText();
        int phone = Integer.parseInt(phoneNumTF.getText());
        String address = addressTF.getText();
        String vin = vinNumTF.getText();
        reservation.setGuest(new Guest(id += 1, name, email, phone, address, vin));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ConfirmationController controller = getLoaderMap().get(SceneName.CONFIRMSCREEN).getController();
        controller.setOriginPoint(originPoint, reservation);
        stage.setScene(getSceneMap().get(SceneName.CONFIRMSCREEN));
        stage.show();
    }


    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (originPoint.equals("homescreen")) {
            stage.setScene(getSceneMap().get(SceneName.HOMESCREEN));
        } else {
            AdminRecordsController controller = getLoaderMap().get(SceneName.ADMINRECORDS).getController();
            controller.setRecordTables();
            stage.setScene(getSceneMap().get(SceneName.ADMINRECORDS));
        }
        stage.show();
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    public void setOriginPoint(String srcOrigin, Reservation srcReservation) {
        this.originPoint = srcOrigin;
        this.reservation = srcReservation;
        clearForm();
    }


    private void clearForm() {
        nameTF.clear();
        emailTF.clear();
        phoneNumTF.clear();
        addressTF.clear();
        vinNumTF.clear();
        nameTF.requestFocus();
    }


    private boolean formEmptyChecker() {
        return  nameTF.getText().isEmpty() ||
                emailTF.getText().isEmpty() ||
                phoneNumTF.getText().isEmpty() ||
                addressTF.getText().isEmpty() ||
                vinNumTF.getText().isEmpty();
    }


    private void showErrorAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error Alert");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String cssPath = getClass().getResource(alertCssPath).toExternalForm();
        if (cssPath != null) dialogPane.getStylesheets().add(cssPath);
        alert.showAndWait();
    }


    private boolean validEmail() {
        String email = emailTF.getText();
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }


    private boolean validAddress() {
        String address = addressTF.getText();
        String addressRegex = "^[\\w\\s.,'-]{5,}$";
        return address.matches(addressRegex);
    }


    private void readGuestIdValue() {
        String idSQL = "SELECT MAX(id) AS max_id FROM Guests";
        try (PreparedStatement st = getConnection().prepareStatement(idSQL);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) id = rs.getInt("max_id");
        } catch (SQLException e) {
            Logging.logException(e, "SQL Error in readGuestIdValue() - Guest Form controller");
        }
    }



    // TextField Listeners ---------------------------------------------------------------------------------------------
    private void setTextFieldToLettersOnly(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z\\s]*")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }


    private void setTextFieldToNumsOnly(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,9}")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }


    private void setVINFieldToLimitedVals(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[A-Za-z0-9]{0,17}")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }
}
