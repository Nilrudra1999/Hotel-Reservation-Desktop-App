/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Main class: Starts the application, sets up the local database connection (SQLite database) and makes the
 * scene builder object. The scene builder object contains the various scene and fxml loader objects used throughout
 * the application, the scene objects are used to set the stage and the fxml loaders are used to set the controllers
 * using their own helper methods. The logging object is an auto logger for app wide activity and exceptions.
 **********************************************************************************************************************/
package app;

import utils.Logging;
import utils.SceneBuilder;
import utils.SceneName;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Main extends Application {
    private static Connection connection;
    private static SceneBuilder sceneBuilder;
    private static final Logging logger = Logging.getInstance();

    @Override public void start(Stage stage) {
        try {
            sceneBuilder = new SceneBuilder();
            stage.setResizable(false);
            stage.setScene(sceneBuilder.getScenes().get(SceneName.HOMESCREEN));
            stage.setTitle("Hotel Northern DevPort Records and Booking System");
            stage.show();
        } catch (IOException e) { logger.logException(e, "IO error while setting scenes"); }
    }


    @Override public void stop() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) { logger.logException(e, "Database connection error while closing"); }
    }


    public static void createConnection() {
        String dbPath = "src/database/HotelReservation.db";
        String url = "jdbc:sqlite:" + dbPath;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) { logger.logException(e, "Database connection error while starting"); }
    }


    public static Logging getLogger() { return logger; }
    public static SceneBuilder getSceneBuilder() {return sceneBuilder; }
    public static Connection getConnection() { return connection; }


    public static void main(String[] args) {
        createConnection();
        launch();
    }
}
