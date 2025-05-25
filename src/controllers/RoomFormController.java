/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Rooms form controller class: Manages the room information for the guests, suggests rooms, and records selection
 * made. Has dynamic form validation for all fields within the forms and final validation upon submission of each form.
 **********************************************************************************************************************/
package controllers;

import models.Reservation;
import models.Room;
import utils.RoomType;
import utils.SceneName;
import utils.Status;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import static app.Main.*;



public class RoomFormController implements Initializable {
    @FXML private TextField roomAutoGenTF;
    @FXML private Spinner<Integer> singleRSpinner, doubleRSpinner;
    @FXML private Spinner<Integer> deluxRSpinner, pentHouseRSpinner;

    private final String alertCssPath = "/styles/alert-styles.css";
    private String originPoint;
    private Reservation reservation;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        setRoomSpinnerValue(singleRSpinner);
        setRoomSpinnerValue(doubleRSpinner);
        setRoomSpinnerValue(deluxRSpinner);
        setRoomSpinnerValue(pentHouseRSpinner);
    }


    public void confirmRooms(ActionEvent event) {
        if (formEmptyChecker()) {
            showErrorAlert("Please use the form to add which rooms you'd refer.");
            return;
        }

        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM Rooms");
            Collection<Room> rooms = loadRoomsFromDB(rs);
            if (!rightAmountOfRooms()) return;
            addRoomsToReservation(rooms);
            if (reservation.getRooms() != null) {
                for (Room room : reservation.getRooms()) room.setStatus(Status.BOOKED);
            } else {
                showErrorAlert("Please ask the front-desk for assistance");
                return;
            }
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error in confirmRooms() - Room Form controller");
        }

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        GuestInfoFormController cn = getSceneBuilder().getLoaders().get(SceneName.GUESTINFOFORM).getController();
        cn.setOriginPoint(originPoint, reservation);
        stage.setScene(getSceneBuilder().getScenes().get(SceneName.GUESTINFOFORM));
        stage.show();
    }


    public void showRegulations(ActionEvent event) {
        String rules = """
                Single Rooms: Max 2 people
                Double Rooms: Max 4 people
                Delux Rooms: Max 2 people but higher price
                Pent-House rooms: Max 2 people but higher price
                -----------------------------------------------
                Recommendation
                -----------------------------------------------
                More than 2 but less than 5 people -> 1 Double Room
                More than 4 people -> Multiple Double Rooms""";
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Room Regulations and Rooms");
        infoAlert.setHeaderText("Hotel Regulations");
        infoAlert.setContentText(rules);
        DialogPane dialogPane = infoAlert.getDialogPane();
        String cssPath = getClass().getResource(alertCssPath).toExternalForm();
        if (cssPath != null) dialogPane.getStylesheets().add(cssPath);
        infoAlert.showAndWait();
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


    public void setOriginPoint(String srcOrigin, Reservation srcReservation) {
        this.originPoint = srcOrigin;
        this.reservation = srcReservation;
        clearForm();
        roomAutoGenTF.setText(calculateRoomsStr());
    }


    private void clearForm() {
        singleRSpinner.getValueFactory().setValue(0);
        doubleRSpinner.getValueFactory().setValue(0);
        deluxRSpinner.getValueFactory().setValue(0);
        pentHouseRSpinner.getValueFactory().setValue(0);
    }


    private String calculateRoomsStr() {
        String roomSuggestion = "";
        if (reservation.getNumOfGuests() == 1 || reservation.getNumOfGuests() == 2) {
            roomSuggestion = "1 Room of any room type will do!!";
        } else if (reservation.getNumOfGuests() > 2 && reservation.getNumOfGuests() < 5) {
            roomSuggestion = "2 Single Rooms or 1 Double Room";
        } else if (reservation.getNumOfGuests() > 4) {
            int numOfSRooms = reservation.getNumOfGuests() / 2;
            int numOfDRooms = reservation.getNumOfGuests() / 4;
            if (reservation.getNumOfGuests() % 2 == 1) {
                numOfSRooms += 1;
                numOfDRooms += 1;
            }
            roomSuggestion = numOfSRooms + " Single Rooms or " + numOfDRooms + " Double Rooms";
        }
        return roomSuggestion;
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


    private boolean formEmptyChecker() {
        int sVal1 = singleRSpinner.getValue();
        int sVal2 = doubleRSpinner.getValue();
        int sVal3 = deluxRSpinner.getValue();
        int sVal4 = pentHouseRSpinner.getValue();
        return sVal1 == 0 && sVal2 == 0 && sVal3 == 0 && sVal4 == 0;
    }


    private boolean rightAmountOfRooms() {
        int totalNumOfGuests = reservation.getNumOfGuests();
        if (singleRSpinner.getValue() > 0) totalNumOfGuests -= (singleRSpinner.getValue() * 2);
        if (doubleRSpinner.getValue() > 0) totalNumOfGuests -= (doubleRSpinner.getValue() * 4);
        if (deluxRSpinner.getValue() > 0) totalNumOfGuests -= (deluxRSpinner.getValue() * 2);
        if (pentHouseRSpinner.getValue() > 0) totalNumOfGuests -= (pentHouseRSpinner.getValue() *2);
        if (reservation.getNumOfGuests() == 1 && totalNumOfGuests == -3 && doubleRSpinner.getValue() == 1) {
            return true;
        } else if (reservation.getNumOfGuests() == 2 && totalNumOfGuests == -2 && doubleRSpinner.getValue() == 1) {
            return true;
        } else if (totalNumOfGuests < -1) {
            showErrorAlert("Too many rooms have been selected");
        } else if (totalNumOfGuests > 1) {
            showErrorAlert("Too few rooms have been selected");
        } else {
            return true;
        }
        return false;
    }


    private Collection<Room> loadRoomsFromDB(ResultSet rs) throws SQLException {
        Collection<Room> rooms = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            RoomType type;
            if (rs.getString(2).equals("single")) type = RoomType.SINGLE;
            else if (rs.getString(2).equals("double")) type = RoomType.DOUBLE;
            else if (rs.getString(2).equals("delux"))  type = RoomType.DELUX;
            else type = RoomType.PENTHOUSE;
            int beds = rs.getInt(3);
            double price = rs.getDouble(4);
            Status roomState;
            if (rs.getString(6).equals("available")) roomState = Status.AVAILABLE;
            else roomState = Status.BOOKED;
            rooms.add(new Room(id, type, beds, price, roomState));
        }
        return rooms;
    }


    private void addRoomsToReservation(Collection<Room> rooms) {
        int sVal1 = singleRSpinner.getValue();
        int sVal2 = doubleRSpinner.getValue();
        int sVal3 = deluxRSpinner.getValue();
        int sVal4 = pentHouseRSpinner.getValue();
        Collection<Room> reservedRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getStatus() == Status.AVAILABLE) {
                if (room.getRoomType() == RoomType.SINGLE && sVal1 > 0) {
                    reservedRooms.add(room);
                    sVal1 -= 1;
                }
                else if (room.getRoomType() == RoomType.DOUBLE && sVal2 > 0) {
                    reservedRooms.add(room);
                    sVal2 -= 1;
                }
                else if (room.getRoomType() == RoomType.DELUX && sVal3 > 0) {
                    reservedRooms.add(room);
                    sVal3 -= 1;
                }
                else if (room.getRoomType() == RoomType.PENTHOUSE && sVal4 > 0) {
                    reservedRooms.add(room);
                    sVal4 -= 1;
                }
            }
        }

        if (reservedRooms.isEmpty()) {
            showErrorAlert("The chosen rooms are not available, Sorry!!");
        } else if (!reservedRooms.isEmpty() && (sVal1 != 0 || sVal2 != 0 || sVal3 != 0 || sVal4 != 0)) {
            showErrorAlert("some of the rooms were available but not all of them.");
        } else {
            reservation.setRooms(reservedRooms);
        }
    }


    private void setRoomSpinnerValue(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4, 0);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(false);
    }
}
