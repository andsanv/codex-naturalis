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

public class PopupStarter {

    @FXML
    private RadioButton backCardButton;

    @FXML
    private RadioButton frontCardButton;

    @FXML
    private Button nextButton;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private void nextButtonClickHandler(MouseEvent mouseEvent) throws IOException {
        //Load popup objective stage
        try {
            FXMLLoader popupObjectiveLoader = new FXMLLoader(getClass().getResource("/view/gui/popupObjective.fxml"));
            Parent popupObjectiveContent = popupObjectiveLoader.load();

            Stage popupObjectiveStage = new Stage();
            popupObjectiveStage.setTitle("Popup Objective Window");
            popupObjectiveStage.setScene(new Scene(popupObjectiveContent));
            popupObjectiveStage.initOwner((Stage) nextButton.getScene().getWindow());  // Set the owner to the main stage
            popupObjectiveStage.initModality(Modality.WINDOW_MODAL);  // Make the popup modal
            ((Stage) nextButton.getScene().getWindow()).close();
            popupObjectiveStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
