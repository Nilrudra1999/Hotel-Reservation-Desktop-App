/***********************************************************************************************************************
 * Hotel Reservation Desktop Application
 *
 * Admin login controller class: Allows the admin or individuals with admin credentials to login into the system,
 * and view the admin level records, simple form with validation and uses database querying to check credentials.
 **********************************************************************************************************************/
package controllers;

import models.Admin;
import utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import static app.Main.*;



public class AdminLoginController implements Initializable {
    @FXML private TextField usernameTF, passwordTF;
    @FXML private ImageView loginImgView;

    private final String alertCssPath = "/styles/alert-styles.css";
    private final String mediaPath = "/media/login-img.jpg";
    private Collection<Admin> admins;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loginImgView.setImage(new Image(getClass().getResource(mediaPath).toExternalForm()));
            loginImgView.setPreserveRatio(false);
        } catch (Exception e) {
            getLogger().logException(e, "General Exception in initialize() - Admin Login controller");
        }
    }


    public void loginAdmin(ActionEvent event) {
        String username = usernameTF.getText();
        String password = passwordTF.getText();
        boolean correctCreds = false;
        for (Admin admin : admins) {
            if (admin.correctUsername(username) && admin.correctPassword(password)) correctCreds = true;
        }
        if (!correctCreds) {
            showErrorAlert("Either one or both credentials entered are incorrect.\nPlease try again.");
        } else {
            getLogger().logActivity("Admin " + username + " logged in at: " + LocalDateTime.now());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            AdminRecordsController cn = getSceneBuilder().getLoaders().get(SceneName.ADMINRECORDS).getController();
            cn.setRecordTables();
            stage.setScene(getSceneBuilder().getScenes().get(SceneName.ADMINRECORDS));
            stage.show();
        }
    }


    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(getSceneBuilder().getScenes().get(SceneName.HOMESCREEN));
        stage.show();
    }


    public void setAdminsToController() {
        loadAdminsFromDb();
        clearForm();
        usernameTF.requestFocus();
    }


    private void loadAdminsFromDb() {
        try (Statement st = getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM Admins");
            admins = new ArrayList<>();
            while (rs.next()) {
                admins.add(new Admin(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            getLogger().logException(e, "SQL Error in loadAdminsFromDb(), admin login controller");
        }
    }


    private void showErrorAlert(String alertMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Login Error Alert");
        alert.setContentText(alertMsg);
        DialogPane dialogPane = alert.getDialogPane();
        String cssPath = getClass().getResource(alertCssPath).toExternalForm();
        if (cssPath != null) dialogPane.getStylesheets().add(cssPath);
        alert.showAndWait();
    }


    private void clearForm() {
        usernameTF.clear();
        passwordTF.clear();
    }
}
