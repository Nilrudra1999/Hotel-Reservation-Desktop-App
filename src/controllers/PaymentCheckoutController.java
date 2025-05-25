/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Payment and checkout controller class: Manages the checkout process for any reservation, contains a form
 * validation and hidden field for adding discount, also transfers new data to the database and adjusts old data.
 **********************************************************************************************************************/
package controllers;

import models.PaymentDetail;
import models.Reservation;
import models.Room;
import utils.PaymentCard;
import utils.SceneName;
import utils.Status;

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
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static app.Main.*;



public class PaymentCheckoutController implements Initializable {
    @FXML private TextField cardNumTF, discountTF, cscTF;
    @FXML private ChoiceBox<String> cardTypeCB;
    @FXML private DatePicker expDatePicker;

    private final String alertCssPath = "/styles/alert-styles.css";
    private Reservation reservation;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        discountTF.setVisible(false);
        cardTypeCB.getItems().addAll(new PaymentCard().getCards());
        setTextFieldToDecimalsOnly(discountTF);
        setTextFieldToNumsOnly(cardNumTF);
        setTextFieldToNumsOnlyForCSC(cscTF);
    }


    public void confirmPayment(ActionEvent event) {
        if (isFormEmpty()) {
            showErrorAlert("Either one or more of the fields is empty\n" +
                           "Or no discount was added after requesting to add discount");
        } else {
            String confirmMsg = "Confirm the following information:\n" +
                                "Card Number: " + cardNumTF.getText() + "\n" +
                                "Card Type: " + cardTypeCB.getValue() + "\n" +
                                "Exp Date: " + expDatePicker.getValue().toString() + "\n\n" +
                                "Ask the guest if they wish to continue";
            if (!showConfirmationAlert(confirmMsg)) return;
            updateReservation();
            addPaymentDetailAndOtherChangesToDb();
            StringBuilder roomsClearedStr = new StringBuilder("These are the rooms which cleared up:\n");
            for (Room room : reservation.getRooms()) {
                roomsClearedStr.append("No: ");
                roomsClearedStr.append(room.getRoomNum());
                roomsClearedStr.append("\n");
            }
            showInfoAlert(String.valueOf(roomsClearedStr));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            AdminRecordsController cn = getSceneBuilder().getLoaders().get(SceneName.ADMINRECORDS).getController();
            cn.setRecordTables();
            stage.setScene(getSceneBuilder().getScenes().get(SceneName.ADMINRECORDS));
            stage.show();
        }
    }


    public void addDiscount(ActionEvent event) {
        discountTF.setVisible(!discountTF.isVisible());
    }


    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        AdminRecordsController cn = getSceneBuilder().getLoaders().get(SceneName.ADMINRECORDS).getController();
        cn.setRecordTables();
        stage.setScene(getSceneBuilder().getScenes().get(SceneName.ADMINRECORDS));
        stage.show();
    }


    public void setFormForPayment(Reservation srcReservation) {
        this.reservation = srcReservation;
        clearForm();
    }


    private void clearForm() {
        cardNumTF.clear();
        cscTF.clear();
        discountTF.clear();
        discountTF.setVisible(false);
        expDatePicker.setValue(null);
        cardTypeCB.getSelectionModel().clearSelection();
    }


    private void showErrorAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Payment Form Error");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String cssPath = getClass().getResource(alertCssPath).toExternalForm();
        if (cssPath != null) dialogPane.getStylesheets().add(cssPath);
        alert.showAndWait();
    }


    private boolean showConfirmationAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Payment Warning");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String alertCSS = getClass().getResource(alertCssPath).toExternalForm();
        if (alertCSS != null) dialogPane.getStylesheets().add(alertCSS);
        return alert.showAndWait().filter(res -> res == ButtonType.OK).isPresent();
    }


    private void showInfoAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Rooms That cleared");
        TextArea textArea = new TextArea(alertMsg);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.getStyleClass().add("custom-textarea");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(textArea);
        String cssPath = getClass().getResource(alertCssPath).toExternalForm();
        if (cssPath != null) dialogPane.getStylesheets().add(cssPath);
        alert.showAndWait();
    }


    private boolean isFormEmpty() {
        boolean noDiscountAdded = discountTF.isVisible() && discountTF.getText().isEmpty();
        return  cardNumTF.getText().isEmpty() ||
                cscTF.getText().isEmpty() ||
                cardTypeCB.getValue() == null ||
                expDatePicker.getValue() == null ||
                noDiscountAdded;
    }


    private void updateReservation() {
        if (discountTF.isVisible()) reservation.getBillingInfo().setDiscount(Double.parseDouble(discountTF.getText()));
        reservation.getBillingInfo().setPaymentDetail(new PaymentDetail(
                Integer.parseInt(cardNumTF.getText()),
                cardTypeCB.getValue(),
                Integer.parseInt(cscTF.getText()),
                expDatePicker.getValue().toString()
        ));
        reservation.setStatus(Status.CLOSED);
    }


    private void addPaymentDetailAndOtherChangesToDb() {
        String cnSQL = "SELECT card_number FROM Payment_Details";
        String payDeSQL = "INSERT INTO Payment_Details (card_number, card_type, csc, exp_date, card_holder) " +
                          "VALUES (?, ?, ?, ?, ?)";
        String billSQL = "UPDATE Bills SET discount = ?, payment_detail = ? WHERE id = ?";
        String resSQL = "UPDATE Reservations SET status = ? WHERE id = ?";
        String roomSQL = "UPDATE Rooms SET reservation_id = NULL, status = ? WHERE id = ?";
        int cardNum = reservation.getBillingInfo().getPaymentDetail().getCardNum();
        boolean existingCard = false;
        try (PreparedStatement cnSt = getConnection().prepareStatement(cnSQL);
             PreparedStatement billSt = getConnection().prepareStatement(billSQL);
             PreparedStatement resSt = getConnection().prepareStatement(resSQL);
             PreparedStatement roomSt = getConnection().prepareStatement(roomSQL);
             PreparedStatement payDSt = getConnection().prepareStatement(payDeSQL);
             ResultSet rs = cnSt.executeQuery()) {
            while (rs.next()) {
                if (rs.getInt("card_number") == cardNum) {
                    existingCard = true;
                    break;
                }
            }

            if (!existingCard) {
                PaymentDetail paymentDetail = reservation.getBillingInfo().getPaymentDetail();
                payDSt.setInt(1, paymentDetail.getCardNum());
                payDSt.setString(2, paymentDetail.getCardType());
                payDSt.setInt(3, paymentDetail.getCsc());
                payDSt.setString(4, paymentDetail.getExpDate());
                payDSt.setInt(5, reservation.getGuest().getId());
                payDSt.executeUpdate();
            }

            billSt.setDouble(1, reservation.getBillingInfo().getDiscount());
            billSt.setInt(2, reservation.getBillingInfo().getPaymentDetail().getCardNum());
            billSt.setInt(3, reservation.getBillingInfo().getId());
            billSt.executeUpdate();
            resSt.setString(1, "closed");
            resSt.setInt(2, reservation.getId());
            resSt.executeUpdate();

            for (Room room : reservation.getRooms()) {
                roomSt.setString(1, "available");
                roomSt.setInt(2, room.getRoomNum());
                roomSt.executeUpdate();
            }
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error when adding db changes, Payment form controller");
        }
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


    private void setTextFieldToNumsOnlyForCSC(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,3}")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }


    private void setTextFieldToDecimalsOnly(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }
}
