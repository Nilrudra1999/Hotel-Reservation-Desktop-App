/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Confirmation controller class: Confirms the information that has been added to the reservation by showing all
 * necessary info to the user, and prompting for confirmation, additionally it calculates the bill in the background.
 **********************************************************************************************************************/
package controllers;

import models.Bill;
import models.Reservation;
import models.Room;
import utils.SceneName;
import utils.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import static app.Main.*;



public class ConfirmationController implements Initializable {
    @FXML private ListView<String> confirmInfoListView;
    @FXML private Text mainMsgText;
    @FXML private ImageView confirmImgView;

    private final String mediaPath = "/media/reservationConfirm-img.jpg";
    private final String alertCssPath = "/styles/alert-styles.css";
    private String originPoint;
    private Reservation reservation;
    private static int id = 0;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            confirmImgView.setImage(new Image(getClass().getResource(mediaPath).toExternalForm()));
            confirmImgView.setPreserveRatio(false);
        } catch (Exception e) {
            getLogger().logException(e, "General Exception in initialize() - Confirmation screen controller");
        }
        readBillingIdValue();
    }


    public void confirmInfo(ActionEvent event) {
        loadDbWithNewReservation();
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


    public void cancelInfo(ActionEvent event) {
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


    public void setOriginPoint(String srcOrigin, Reservation srcReservation) {
        this.originPoint = srcOrigin;
        this.reservation = srcReservation;
        String msgText = "Thank you for choosing Hotel Northern DevPort\n" +
                         "Please check the below information, and only confirm if correct.";
        mainMsgText.setText(msgText);
        setListView();
    }


    private void setListView() {
        addBillingInfoToReservation();
        ObservableList<String> confirmInfo = FXCollections.observableArrayList();
        confirmInfo.add("Your Reservation information -------");
        confirmInfo.add("Check-in Date: " + reservation.getCheckInDate());
        confirmInfo.add("Check-out Date: " + reservation.getCheckOutDate());
        confirmInfo.add("The number of guests: " + reservation.getNumOfGuests());
        confirmInfo.add("\nYour Room information -------");
        for (Room room : reservation.getRooms()) {
            confirmInfo.add("Room ID: " + room.getRoomNum() + " Room type: " + room.getRoomType());
            confirmInfo.add("Price: $" + room.getPrice() + " and Number of beds: " + room.getNumOfBeds());
            confirmInfo.add(" ");
        }
        confirmInfo.add("\nYour information -------");
        confirmInfo.add("Name: " + reservation.getGuest().getName());
        confirmInfo.add("Phone Number: " + reservation.getGuest().getPhoneNumber());
        confirmInfo.add("Address: " + reservation.getGuest().getAddress());
        confirmInfo.add("\nYour Billing information -------");
        confirmInfo.add("Subtotal Amount: $" + String.format("%.2f", reservation.getBillingInfo().getSubTotal()));
        confirmInfo.add("HST Amount: $" + String.format("%.2f", reservation.getBillingInfo().getHst()));
        confirmInfo.add("Total Amount: $" + String.format("%.2f", reservation.getBillingInfo().getTotal()));
        confirmInfoListView.setItems(confirmInfo);
    }


    private void addBillingInfoToReservation() {
        LocalDate checkin = LocalDate.parse(reservation.getCheckInDate());
        LocalDate checkout = LocalDate.parse(reservation.getCheckOutDate());
        double weeksBetween = (double) (ChronoUnit.DAYS.between(checkin, checkout)) / 7.0;
        double roomsTotal = 0.0;
        for (Room room : reservation.getRooms()) roomsTotal += room.getPrice();
        double subtotal = roomsTotal * weeksBetween;
        double hst = subtotal * 0.13;
        double serviceCharge = 1300.55;
        double total = subtotal + hst + serviceCharge;
        reservation.setBillingInfo(new Bill(id += 1, subtotal, hst, total));
    }


    private void readBillingIdValue() {
        String idSQL = "SELECT MAX(id) AS max_id FROM Bills";
        try (PreparedStatement st = getConnection().prepareStatement(idSQL);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) id = rs.getInt("max_id");
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error in readBillingIdValue(), Confirmation screen controller");
        }
    }


    private void loadDbWithNewReservation() {
        String guestSQL = "INSERT INTO Guests (id, name, phone_number, address, email, vin_number) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
        String billSQL = "INSERT INTO Bills (id, reservation_details, sub_total, hst, total) VALUES (?, ?, ?, ?, ?)";
        String resSQL = "INSERT INTO Reservations " +
                        "(id, guest, bill, check_in_date, check_out_date, total_guests, status, admin) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String roomSQL = "UPDATE Rooms SET reservation_id = ?, status = ? WHERE id = ?";
        try (PreparedStatement guestST = getConnection().prepareStatement(guestSQL);
             PreparedStatement billST = getConnection().prepareStatement(billSQL);
             PreparedStatement resST = getConnection().prepareStatement(resSQL);
             PreparedStatement roomST = getConnection().prepareStatement(roomSQL)) {
            guestST.setInt(1, reservation.getGuest().getId());
            guestST.setString(2, reservation.getGuest().getName());
            guestST.setInt(3, reservation.getGuest().getPhoneNumber());
            guestST.setString(4, reservation.getGuest().getAddress());
            guestST.setString(5, reservation.getGuest().getEmail());
            guestST.setString(6, reservation.getGuest().getVinNumber());
            guestST.executeUpdate();

            billST.setInt(1, reservation.getBillingInfo().getId());
            billST.setInt(2, reservation.getId());
            billST.setDouble(3, reservation.getBillingInfo().getSubTotal());
            billST.setDouble(4, reservation.getBillingInfo().getHst());
            billST.setDouble(5, reservation.getBillingInfo().getTotal());
            billST.executeUpdate();

            resST.setInt(1, reservation.getId());
            resST.setInt(2, reservation.getGuest().getId());
            resST.setInt(3, reservation.getBillingInfo().getId());
            resST.setString(4, reservation.getCheckInDate());
            resST.setString(5, reservation.getCheckOutDate());
            resST.setInt(6, reservation.getNumOfGuests());
            if (reservation.getStatus() == Status.OPEN) resST.setString(7, "open");
            else resST.setString(7, "closed");
            resST.setInt(8, 1);
            resST.executeUpdate();

            for (Room room : reservation.getRooms()) {
                roomST.setInt(1, reservation.getId());
                roomST.setString(2, "booked");
                roomST.setInt(3, room.getRoomNum());
                roomST.executeUpdate();
            }
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error while loading the database, Confirmation screen controller");
        }
    }
}
