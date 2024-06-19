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

public class PopupTokens {


    @FXML
    private RadioButton blueTokenButton;

    @FXML
    private RadioButton greenTokenButton;

    @FXML
    private Button next3Button;

    @FXML
    private RadioButton redTokenButton;

    @FXML
    private ToggleGroup toggleGroup3;

    @FXML
    private RadioButton yellowTokenButton;

    @FXML
    private void nextButtonClickHandler(MouseEvent mouseEvent) throws IOException {
        //Load popup tokens stage
        ((Stage) next3Button.getScene().getWindow()).close();

    }

}
