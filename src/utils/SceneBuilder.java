/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Scene builder utility class: This class builds the scene and fxml objects used throughout the application, the
 * class encapsulated and abstracts away the total scenes and fxml objects as well as their initialization, while
 * providing a simple enum based interface to get various fxml loaders and scenes.
 **********************************************************************************************************************/
package utils;

import controllers.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class SceneBuilder {
    private static final Map<SceneName, FXMLLoader> loaderMap = new HashMap<>();
    private static final Map<SceneName, Scene> sceneMap = new HashMap<>();

    public SceneBuilder() throws IOException {
        String scene1 = "/views/homeScreen-view.fxml";
        String scene2 = "/views/checkInForm-view.fxml";
        String scene3 = "/views/roomInfoForm-view.fxml";
        String scene4 = "/views/guestDetailsForm-view.fxml";
        String scene5 = "/views/confirmation-view.fxml";
        String scene6 = "/views/adminLogin-view.fxml";
        String scene7 = "/views/adminRecords-view.fxml";
        String scene8 = "/views/paymentForm-view.fxml";

        loaderMap.put(SceneName.HOMESCREEN, new FXMLLoader(HomeScreenController.class.getResource(scene1)));
        loaderMap.put(SceneName.CHECKINFORM, new FXMLLoader(CheckInFormController.class.getResource(scene2)));
        loaderMap.put(SceneName.ROOMSFORM, new FXMLLoader(RoomFormController.class.getResource(scene3)));
        loaderMap.put(SceneName.GUESTINFOFORM, new FXMLLoader(GuestInfoFormController.class.getResource(scene4)));
        loaderMap.put(SceneName.CONFIRMSCREEN, new FXMLLoader(ConfirmationController.class.getResource(scene5)));
        loaderMap.put(SceneName.ADMINLOGIN, new FXMLLoader(AdminLoginController.class.getResource(scene6)));
        loaderMap.put(SceneName.ADMINRECORDS, new FXMLLoader(AdminRecordsController.class.getResource(scene7)));
        loaderMap.put(SceneName.PAYMENTFORM, new FXMLLoader(PaymentCheckoutController.class.getResource(scene8)));

        sceneMap.put(SceneName.HOMESCREEN, new Scene(loaderMap.get(SceneName.HOMESCREEN).load()));
        sceneMap.put(SceneName.CHECKINFORM, new Scene(loaderMap.get(SceneName.CHECKINFORM).load()));
        sceneMap.put(SceneName.ROOMSFORM, new Scene(loaderMap.get(SceneName.ROOMSFORM).load()));
        sceneMap.put(SceneName.GUESTINFOFORM, new Scene(loaderMap.get(SceneName.GUESTINFOFORM).load()));
        sceneMap.put(SceneName.CONFIRMSCREEN, new Scene(loaderMap.get(SceneName.CONFIRMSCREEN).load()));
        sceneMap.put(SceneName.ADMINLOGIN, new Scene(loaderMap.get(SceneName.ADMINLOGIN).load()));
        sceneMap.put(SceneName.ADMINRECORDS, new Scene(loaderMap.get(SceneName.ADMINRECORDS).load()));
        sceneMap.put(SceneName.PAYMENTFORM, new Scene(loaderMap.get(SceneName.PAYMENTFORM).load()));
    }


    public Map<SceneName, FXMLLoader> getLoaders() { return loaderMap; }
    public Map<SceneName, Scene> getScenes() { return sceneMap; }
}
