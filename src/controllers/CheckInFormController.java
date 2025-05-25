/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Check-in form controller class: Manages the initial reservation information for guests checking into the hotel. Has
 * dynamic form validation for all fields within the forms and final validation upon submission of each form.
 **********************************************************************************************************************/
package controllers;

import models.Reservation;
import utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static app.Main.*;



public class CheckInFormController implements Initializable {
    @FXML private TextField numOfPeopleTF;
    @FXML private DatePicker checkInDatePicker, checkOutDatePicker;

    private final String alertCssPath = "/styles/alert-styles.css";
    private String originPoint;
    private static int id = 0;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        numbersOnlyTextField(numOfPeopleTF);
        readReservationIdValue();
    }


    public void confirmCheckIn(ActionEvent event) {
        LocalDate checkin = checkInDatePicker.getValue();
        LocalDate checkout = checkOutDatePicker.getValue();
        if (numOfPeopleTF.getText().isEmpty() || checkin == null || checkout == null) {
            showErrorAlert("Either one or more of the required fields are empty, please check the form again.");
            return;
        } else if (checkin.isBefore(LocalDate.now())) {
            showErrorAlert("Check-in date cannot be earlier than today's date.");
            return;
        } else if (!checkout.isAfter(checkin)) {
            showErrorAlert("Check-out date must be after the specified check-in date.");
            return;
        }
        String checkInDate = checkin.toString();
        String checkOutDate = checkout.toString();
        int numPeople = Integer.parseInt(numOfPeopleTF.getText());
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        RoomFormController cn = getSceneBuilder().getLoaders().get(SceneName.ROOMSFORM).getController();
        cn.setOriginPoint(originPoint, new Reservation(id += 1, checkInDate, checkOutDate, numPeople));
        stage.setScene(getSceneBuilder().getScenes().get(SceneName.ROOMSFORM));
        stage.show();
    }


    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if (originPoint.equals("homescreen")) {
            stage.setScene(getSceneBuilder().getScenes().get(SceneName.HOMESCREEN));
        } else {
            AdminRecordsController cn = getSceneBuilder().getLoaders().get(SceneName.ADMINRECORDS).getController();
            cn.setRecordTables();
            stage.setScene(getSceneBuilder().getScenes().get(SceneName.ADMINRECORDS));
        }
        stage.show();
    }


    public void setOriginPoint(String srcOrigin) {
        this.originPoint = srcOrigin;
        clearForm();
    }


    private void clearForm() {
        numOfPeopleTF.clear();
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
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


    private void readReservationIdValue() {
        String idSQL = "SELECT MAX(id) AS max_id FROM Reservations";
        try (PreparedStatement st = getConnection().prepareStatement(idSQL);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) id = rs.getInt("max_id");
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error in readReservationIdValue() - Check in form controller");
        }
    }


    private void numbersOnlyTextField(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }
}
