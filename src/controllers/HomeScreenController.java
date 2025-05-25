/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Homescreen controller class: Controls the welcome screen, displays the video walkthrough to the users when they
 * open the application, and also transfers control to either the admin login controller or reservation forms
 * controllers starting at the rooms information form controller.
 **********************************************************************************************************************/
package controllers;

import utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static app.Main.*;



public class HomeScreenController implements Initializable {
    @FXML private MediaView instructionVid;

    private final String mediaPath = "/media/videoGuide-vid.mp4";
    private final String alertCssPath = "/styles/alert-styles.css";
    private MediaPlayer mediaPlayer;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) { loadMedia(); }


    public void makeReservation(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        CheckInFormController cn = getSceneBuilder().getLoaders().get(SceneName.CHECKINFORM).getController();
        cn.setOriginPoint("homescreen");
        stage.setScene(getSceneBuilder().getScenes().get(SceneName.CHECKINFORM));
        stage.show();
    }


    public void adminLogin(ActionEvent event) {
        String warningText = "You're attempting to enter an area of this application reserved for " +
                             "admins only. Do you still wish to continue?";
        if (showLoginAlert(warningText)) {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            AdminLoginController cn = getSceneBuilder().getLoaders().get(SceneName.ADMINLOGIN).getController();
            cn.setAdminsToController();
            stage.setScene(getSceneBuilder().getScenes().get(SceneName.ADMINLOGIN));
            stage.show();
        }
    }


    private void loadMedia() {
        try {
            Media media = new Media(getClass().getResource(mediaPath).toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            instructionVid.setMediaPlayer(mediaPlayer);
            instructionVid.setPreserveRatio(false);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setOnReady(mediaPlayer::play);
        } catch (Exception e) {
            getLogger().logException(e, "General Exception in loadMedia(), Home screen controller");
        }
    }


    private boolean showLoginAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Admin Login Warning");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String alertCSS = getClass().getResource(alertCssPath).toExternalForm();
        if (alertCSS != null) dialogPane.getStylesheets().add(alertCSS);
        return alert.showAndWait().filter(res -> res == ButtonType.OK).isPresent();
    }
}
