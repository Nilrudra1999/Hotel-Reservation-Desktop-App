/***********************************************************************************************************************
 * Main Class: Starting point of the application, initializes fxml loaders, scenes, and a database connection.
 * Then loads the first scene into the stage and passes control over to the first scene's controller.
 **********************************************************************************************************************/
package app;

import utils.Logging;
import utils.SceneName;
import controllers.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private static Map<SceneName, FXMLLoader> loaderMap = new HashMap<>();
    private static Map<SceneName, Scene> sceneMap = new HashMap<>();
    private static Connection connection;

    @Override public void start(Stage stage) {
        String scene1 = "/views/homeScreen-view.fxml";
        String scene2 = "/views/checkInForm-view.fxml";
        String scene3 = "/views/roomInfoForm-view.fxml";
        String scene4 = "/views/guestDetailsForm-view.fxml";
        String scene5 = "/views/confirmation-view.fxml";
        String scene6 = "/views/adminLogin-view.fxml";
        String scene7 = "/views/adminRecords-view.fxml";
        String scene8 = "/views/paymentForm-view.fxml";
        String scene9 = "/views/feedback-view.fxml";
        try {
            loaderMap.put(SceneName.HOMESCREEN, new FXMLLoader(HomeScreenController.class.getResource(scene1)));
            loaderMap.put(SceneName.CHECKINFORM, new FXMLLoader(CheckInFormController.class.getResource(scene2)));
            loaderMap.put(SceneName.ROOMSFORM, new FXMLLoader(RoomFormController.class.getResource(scene3)));
            loaderMap.put(SceneName.GUESTINFOFORM, new FXMLLoader(GuestInfoFormController.class.getResource(scene4)));
            loaderMap.put(SceneName.CONFIRMSCREEN, new FXMLLoader(ConfirmationController.class.getResource(scene5)));
            loaderMap.put(SceneName.ADMINLOGIN, new FXMLLoader(AdminLoginController.class.getResource(scene6)));
            loaderMap.put(SceneName.ADMINRECORDS, new FXMLLoader(AdminRecordsController.class.getResource(scene7)));
            loaderMap.put(SceneName.PAYMENTFORM, new FXMLLoader(PaymentCheckoutController.class.getResource(scene8)));
            loaderMap.put(SceneName.FEEDBACKFORM, new FXMLLoader(FeedbackController.class.getResource(scene9)));

            sceneMap.put(SceneName.HOMESCREEN, new Scene(loaderMap.get(SceneName.HOMESCREEN).load()));
            sceneMap.put(SceneName.CHECKINFORM, new Scene(loaderMap.get(SceneName.CHECKINFORM).load()));
            sceneMap.put(SceneName.ROOMSFORM, new Scene(loaderMap.get(SceneName.ROOMSFORM).load()));
            sceneMap.put(SceneName.GUESTINFOFORM, new Scene(loaderMap.get(SceneName.GUESTINFOFORM).load()));
            sceneMap.put(SceneName.CONFIRMSCREEN, new Scene(loaderMap.get(SceneName.CONFIRMSCREEN).load()));
            sceneMap.put(SceneName.ADMINLOGIN, new Scene(loaderMap.get(SceneName.ADMINLOGIN).load()));
            sceneMap.put(SceneName.ADMINRECORDS, new Scene(loaderMap.get(SceneName.ADMINRECORDS).load()));
            sceneMap.put(SceneName.PAYMENTFORM, new Scene(loaderMap.get(SceneName.PAYMENTFORM).load()));
            sceneMap.put(SceneName.FEEDBACKFORM, new Scene(loaderMap.get(SceneName.FEEDBACKFORM).load()));

            stage.setResizable(false);
            stage.setScene(sceneMap.get(SceneName.HOMESCREEN));
            stage.setTitle("Hotel Northern DevPort Records and Booking System");
            stage.show();
        } catch (IOException e) {
            Logging.logException(e, "IO Error in start() - Main class");
        }
    }



    @Override public void stop() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            Logging.logException(e, "Database Error in stop() - Main Class");
        }
    }



    public static Map<SceneName, FXMLLoader> getLoaderMap() { return loaderMap; }
    public static Map<SceneName, Scene> getSceneMap() { return sceneMap; }
    public static Connection getConnection() { return connection; }



    public static void main(String[] args) {
        String dbPath = "src/database/HotelReservation.db";
        String url = "jdbc:sqlite:" + dbPath;
        try {
            connection = DriverManager.getConnection(url);
            launch();
        } catch (SQLException e) {
            Logging.logException(e, "Database Error in main() - Main Class");
        }
    }
}
