/***********************************************************************************************************************
 * Admin Records Controller: Controls the admin level records for the whole system, has the ability to display guests,
 * bookings, billing information records and also allows the user to add a new record or delete an old one.
 **********************************************************************************************************************/
package controllers;

import models.Bill;
import models.Guest;
import models.Reservation;
import models.Room;
import utils.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static app.Main.*;

public class AdminRecordsController implements Initializable {
    @FXML private TextField bookingsTF, customerTF, billingTF;
    @FXML private Text recordTitleText;
    @FXML private Tab bookingsTab, customerTab, billingTab;
    @FXML private TabPane recordsTabPane;
    @FXML private TableView<Reservation> bookingsTableView, customerTableView, billingTableView;

    @FXML private TableColumn<Reservation, Integer> customerIDCol, roomNumCol, numOfPeopleCol;
    @FXML private TableColumn<Reservation, String> roomTypesCol, checkDatesCol;

    @FXML private TableColumn<Reservation, String> customerNameCol, emailCol, addressCol, vinNumCol;
    @FXML private TableColumn<Reservation, Integer> phoneNumCol;

    @FXML private TableColumn<Reservation, Integer> billingNumCol;
    @FXML private TableColumn<Reservation, Double> subTotalCol, hstCol, totalCol, discountCol;

    private final String alertCssPath = "/ca/senecacollege/cpa/app/styles/alert-styles.css";
    private ObservableList<Reservation> reservations;

    // Initializable Method --------------------------------------------------------------------------------------------
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFieldToNumsOnly(bookingsTF);
        setTextFieldToNumsOnly(billingTF);
        searchByRoomNum(bookingsTF);
        searchByNameOrVinNum(customerTF);
        searchByBillingNumber(billingTF);
        changeRecordTitleBasedOnTab(recordsTabPane.getSelectionModel().getSelectedItem());
        recordsTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newTab) -> changeRecordTitleBasedOnTab(newTab)
        );
        formatDoubleColumn(subTotalCol);
        formatDoubleColumn(hstCol);
        formatDoubleColumn(totalCol);
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    public void addRecord(ActionEvent event) {
        Logging.logActivity("Admin Added a new Reservation at " + LocalDateTime.now());
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        CheckInFormController controller = getLoaderMap().get(SceneName.CHECKINFORM).getController();
        controller.setOriginPoint("adminRecords");
        stage.setScene(getSceneMap().get(SceneName.CHECKINFORM));
        stage.show();
    }


    public void checkoutGuest(ActionEvent event) {
        Reservation resToCheckout = getTheReservation();
        if (resToCheckout == null) {
            showErrorAlert("No Reservation record was selected.\nPlease pick a reservation to checkout.");
        } else if (resToCheckout.getRooms() == null) {
            showErrorAlert("This reservation has already been paid and closed.\n Please select a valid option.");
            clearTableSelections();
        } else {
            Logging.logActivity("Admin Checked Guest: " + resToCheckout.getGuest().getName() +
                                " out at " + LocalDateTime.now());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            PaymentCheckoutController controller = getLoaderMap().get(SceneName.PAYMENTFORM).getController();
            controller.setFormForPayment(resToCheckout);
            stage.setScene(getSceneMap().get(SceneName.PAYMENTFORM));
            stage.show();
        }
    }


    public void deleteRecord(ActionEvent event) {
        Reservation resToDel = getTheReservation();
        if (resToDel == null) {
            showErrorAlert("No Reservation record was selected.\nPlease pick a reservation to delete.");
        } else {
            if (showDeletionAlert("This Reservation Record and all corresponding Information will be deleted." +
                                  "Do you still want to proceed?")) {
                Logging.logActivity("Admin deleted a record of reservation with ID: " + resToDel.getId() +
                                    " and under guest name: " + resToDel.getGuest().getName() +
                                    " at " + LocalDateTime.now());
                deleteTableRecordsAndUpdateAppTables(resToDel);
            }
            else clearTableSelections();
        }
    }


    public void exitRecords(ActionEvent event) {
        if (showExitingAlert("Are you sure you want to leave and return to homescreen?")) {
            Logging.logActivity("Admin logged out at " + LocalDateTime.now());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(getSceneMap().get(SceneName.HOMESCREEN));
            stage.show();
        }
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    public void setRecordTables() {
        reservations = loadTableDataWithSQL();
        // setting bookings info
        customerIDCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getGuest().getId()).asObject());
        roomNumCol.setCellValueFactory(cell -> {
            Room firstRoom = cell.getValue().getFirstRoom();
            if (firstRoom != null) return new SimpleIntegerProperty(firstRoom.getRoomNum()).asObject();
            else return null;
        });
        roomTypesCol.setCellValueFactory(cell -> {
            Room firstRoom = cell.getValue().getFirstRoom();
            if (firstRoom != null) return new SimpleStringProperty(firstRoom.getStringStatus());
            else return new SimpleStringProperty("closed");
        });
        numOfPeopleCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getNumOfGuests()).asObject());
        checkDatesCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getcompleteDateAsString()));
        bookingsTableView.setItems(reservations);
        // setting guest info
        customerNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGuest().getName()));
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGuest().getEmail()));
        phoneNumCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getGuest().getPhoneNumber()).asObject());
        addressCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGuest().getAddress()));
        vinNumCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGuest().getVinNumber()));
        customerTableView.setItems(reservations);
        // setting billing info
        billingNumCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getBillingInfo().getId()).asObject());
        subTotalCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getBillingInfo().getSubTotal()).asObject());
        hstCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getBillingInfo().getHst()).asObject());
        totalCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getBillingInfo().getTotal()).asObject());
        discountCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getBillingInfo().getDiscount()).asObject());
        billingTableView.setItems(reservations);
        clearTableSelections();
    }


    private void deleteTableRecordsAndUpdateAppTables(Reservation resDel) {
        String roomResetSQL = "UPDATE Rooms SET reservation_id = NULL, status = 'available' " +
                              "WHERE reservation_id = " + resDel.getId();
        String resDelSQL = "DELETE FROM Reservations WHERE id = " + resDel.getId();
        String guestDelSQL = "DELETE FROM Guests WHERE id = " + resDel.getGuest().getId();
        String billDelSQL = "DELETE FROM Bills WHERE id = " + resDel.getBillingInfo().getId();
        try (PreparedStatement roomSt = getConnection().prepareStatement(roomResetSQL);
             PreparedStatement resSt = getConnection().prepareStatement(resDelSQL);
             PreparedStatement guestSt = getConnection().prepareStatement(guestDelSQL);
             PreparedStatement billSt = getConnection().prepareStatement(billDelSQL)) {
            roomSt.executeUpdate();
            resSt.executeUpdate();
            guestSt.executeUpdate();
            billSt.executeUpdate();
        } catch (SQLException e) {
            Logging.logException(e, "SQL Error in deleteTableRecordsAndUpdateAppTables() - Admin records controller");
        }
        setRecordTables();
    }


    private void clearTableSelections() {
        bookingsTableView.getSelectionModel().clearSelection();
        customerTableView.getSelectionModel().clearSelection();
        billingTableView.getSelectionModel().clearSelection();
    }


    private ObservableList<Reservation> loadTableDataWithSQL() {
        reservations = FXCollections.observableArrayList();
        String roomQuery = "SELECT * FROM Rooms WHERE reservation_id IS NOT NULL";
        try (Statement guestSt = getConnection().createStatement();
             Statement billSt = getConnection().createStatement();
             Statement resSt = getConnection().createStatement();
             PreparedStatement roomSt = getConnection().prepareStatement(roomQuery)) {
            ResultSet rs1 = guestSt.executeQuery("SELECT * FROM Guests");
            ResultSet rs2 = billSt.executeQuery("SELECT * FROM Bills");
            ResultSet rs3 = resSt.executeQuery("SELECT * FROM Reservations");
            ResultSet rs4 = roomSt.executeQuery();
            Map<Integer, Guest> guestMap = setMapOfGuests(rs1);
            Map<Integer, Bill> billMap = setMapOfBills(rs2);
            Map<Integer, ArrayList<Room>> roomMap = setMapOfRooms(rs4);
            while (rs3.next()) {
                Reservation res = new Reservation(rs3.getInt(1), rs3.getString(4), rs3.getString(5), rs3.getInt(6));
                res.setGuest(guestMap.get(rs3.getInt(2)));
                res.setBillingInfo(billMap.get(rs3.getInt(3)));
                res.setRooms(roomMap.get(rs3.getInt(1)));
                reservations.add(res);
            }
        } catch (SQLException e) {
            Logging.logException(e, "SQL Error in loadTableDataWithSQL() - Admin records controller");
        }
        return reservations;
    }


    private Map<Integer, Guest> setMapOfGuests(ResultSet rs) throws SQLException {
        Map<Integer, Guest> guestMap = new HashMap<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            guestMap.put(id,new Guest(
                    id,
                    rs.getString(2),
                    rs.getString(5),
                    rs.getInt(3),
                    rs.getString(4),
                    rs.getString(6)
            ));
        }
        return guestMap;
    }


    private Map<Integer, Bill> setMapOfBills(ResultSet rs) throws SQLException {
        Map<Integer, Bill> billMap = new HashMap<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            billMap.put(id, new Bill(
                    id,
                    rs.getDouble(3),
                    rs.getDouble(4),
                    rs.getDouble(5)
            ));
        }
        return billMap;
    }


    private Map<Integer, ArrayList<Room>> setMapOfRooms(ResultSet rs) throws SQLException {
        Map<Integer, ArrayList<Room>> roomMap = new HashMap<>();
        while (rs.next()) {
            int resId = rs.getInt(5);
            RoomType type;
            if (rs.getString(2).equals("single")) type = RoomType.SINGLE;
            else if (rs.getString(2).equals("double")) type = RoomType.DOUBLE;
            else if (rs.getString(2).equals("delux"))  type = RoomType.DELUX;
            else type = RoomType.PENTHOUSE;
            Status roomState;
            if (rs.getString(6).equals("available")) roomState = Status.AVAILABLE;
            else roomState = Status.BOOKED;
            Room room = new Room(
                    rs.getInt(1),
                    type,
                    rs.getInt(3),
                    rs.getDouble(4),
                    roomState
            );
            roomMap.computeIfAbsent(resId, k -> new ArrayList<>()).add(room);
        }
        return roomMap;
    }


    private boolean showExitingAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Exiting Admin Records?");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String alertCSS = getClass().getResource(alertCssPath).toExternalForm();
        if (alertCSS != null) dialogPane.getStylesheets().add(alertCSS);
        return alert.showAndWait().filter(res -> res == ButtonType.OK).isPresent();
    }


    private boolean showDeletionAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Record Deletion Warning");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String alertCSS = getClass().getResource(alertCssPath).toExternalForm();
        if (alertCSS != null) dialogPane.getStylesheets().add(alertCSS);
        return alert.showAndWait().filter(res -> res == ButtonType.OK).isPresent();
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


    private void changeRecordTitleBasedOnTab(Tab tab) {
        if (tab == bookingsTab) recordTitleText.setText("Bookings Records");
        else if (tab == customerTab) recordTitleText.setText("Customer Records");
        else if (tab == billingTab) recordTitleText.setText("Billing Records");
    }


    private Reservation getTheReservation() {
        Reservation res = null;
        if (bookingsTableView.getSelectionModel().getSelectedItem() != null) {
            res = bookingsTableView.getSelectionModel().getSelectedItem();
        } else if (customerTableView.getSelectionModel().getSelectedItem() != null) {
            res = customerTableView.getSelectionModel().getSelectedItem();
        } else if (billingTableView.getSelectionModel().getSelectedItem() != null) {
            res = billingTableView.getSelectionModel().getSelectedItem();
        }
        return res;
    }



    // TextField Listeners ---------------------------------------------------------------------------------------------
    private void searchByRoomNum(TextField textField) {
        textField.textProperty().addListener((_, _, newStr) -> {
            if (newStr.isEmpty()) bookingsTableView.setItems(reservations);
            else {
                try {
                    int roomNum = Integer.parseInt(newStr);
                    ObservableList<Reservation> filteredList = reservations.stream()
                            .filter(res -> {
                                Collection<Room> rooms = res.getRooms();
                                return rooms != null && rooms.stream().anyMatch(room ->
                                        room != null && room.getRoomNum() == roomNum
                                );
                            })
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
                    bookingsTableView.setItems(filteredList);
                } catch (NumberFormatException e) {
                    bookingsTableView.setItems(FXCollections.emptyObservableList());
                }
            }
        });
    }


    private void searchByNameOrVinNum(TextField textField) {
        textField.textProperty().addListener((_, _, newStr) -> {
            if (newStr.isEmpty()) customerTableView.setItems(reservations);
            else {
                ObservableList<Reservation> filteredList = reservations.stream()
                        .filter(res -> {
                            Guest guest = res.getGuest();
                            return  guest != null && (guest.getName().toLowerCase().contains(newStr.toLowerCase()) ||
                                    guest.getVinNumber().toLowerCase().contains(newStr.toLowerCase()));
                        }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                customerTableView.setItems(filteredList);
            }
        });
    }


    private void searchByBillingNumber(TextField textField) {
        textField.textProperty().addListener((_, _, newStr) -> {
            if (newStr.isEmpty()) billingTableView.setItems(reservations);
            else {
                try {
                    int billingNum = Integer.parseInt(newStr);
                    ObservableList<Reservation> filteredList = reservations.stream()
                            .filter(res -> {
                                Bill billingInfo = res.getBillingInfo();
                                return billingInfo != null && billingInfo.getId() == billingNum;
                            }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                    billingTableView.setItems(filteredList);
                } catch (NumberFormatException e) { billingTableView.setItems(FXCollections.emptyObservableList()); }
            }
        });
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



    // TableView Formatter ---------------------------------------------------------------------------------------------
    private <T> void formatDoubleColumn(TableColumn<T, Double> column) {
        column.setCellFactory(doubleTableColumn -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%.2f", price));
            }
        });
    }
}
