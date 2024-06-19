package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PopupObjective {

    @FXML
    private RadioButton firstObjectiveButton;

    @FXML
    private Button next2Button;

    @FXML
    private RadioButton secondObjectiveButton;

    @FXML
    private ToggleGroup toggleGroup2;

    @FXML
    private void nextButtonClickHandler(MouseEvent mouseEvent) throws IOException {
        //Load popup objective stage
        try {
            FXMLLoader popupTokensLoader = new FXMLLoader(getClass().getResource("/view/gui/popupTokens.fxml"));
            Parent popupTokensContent = popupTokensLoader.load();

            Stage popupTokensStage = new Stage();
            popupTokensStage.setTitle("Popup Tokens Window");
            popupTokensStage.setScene(new Scene(popupTokensContent));
            popupTokensStage.initOwner((Stage) next2Button.getScene().getWindow());  // Set the owner to the main stage
            popupTokensStage.initModality(Modality.WINDOW_MODAL);  // Make the popup modal
            ((Stage) next2Button.getScene().getWindow()).close();
            popupTokensStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
