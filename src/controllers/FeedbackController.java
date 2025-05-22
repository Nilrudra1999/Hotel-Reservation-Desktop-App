/***********************************************************************************************************************
 * Feedback Controller: Accepts feedback from the guests or admin depending on who is using the form, stores the
 * comments, ratings, the reservation, and the guest id
 **********************************************************************************************************************/
package controllers;

import utils.SceneName;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static app.Main.getLoaderMap;
import static app.Main.getSceneMap;

public class FeedbackController implements Initializable {
    @FXML private TextField ratingTF;
    @FXML private TextArea commentsTA;

    // Initializable Method --------------------------------------------------------------------------------------------
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFieldToNumsOnly(ratingTF);
    }



    // Event Handlers --------------------------------------------------------------------------------------------------
    public void confirmComments(ActionEvent event) {
        // todo confirm data and save to db
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        AdminRecordsController controller = getLoaderMap().get(SceneName.ADMINRECORDS).getController();
        controller.setRecordTables();
        stage.setScene(getSceneMap().get(SceneName.ADMINRECORDS));
        stage.show();
    }


    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        AdminRecordsController controller = getLoaderMap().get(SceneName.ADMINRECORDS).getController();
        controller.setRecordTables();
        stage.setScene(getSceneMap().get(SceneName.ADMINRECORDS));
        stage.show();
    }



    // Helper Methods --------------------------------------------------------------------------------------------------
    public void setForm() {
        clearForm();
        ratingTF.requestFocus();
    }


    private void clearForm() {
        ratingTF.clear();
        commentsTA.clear();
    }


    private boolean verifyInfo() {
        // todo
        return false;
    }


    private void loadToDb() {
        // todo
    }



    // TextField Listeners ---------------------------------------------------------------------------------------------
    private void setTextFieldToNumsOnly(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,1}")) return change;
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }
}
