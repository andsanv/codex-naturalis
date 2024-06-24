package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class ConfigController extends Controller {
    public ConfigController(GUI gui) {
        super(gui);
    }

    @FXML
    private VBox VboxCentral;

    @FXML
    private GridPane gridPane;

    @FXML
    private Text nicknameText;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Button submitNickname;

    @FXML
    private void handleSubmitClick(MouseEvent event) {
        try {
            String nickname = nicknameTextField.getText();
            if (!nickname.isEmpty()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/tempGameView.fxml"));
                Parent root = fxmlLoader.load();
                GameController gameController = (GameController) fxmlLoader.getController();
                gameController.initialize();
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                // String url = Objects.requireNonNull(getClass().getResource("/css/menuPane.css")).toExternalForm();
                // scene.getStylesheets().add(url);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnterKeyPressed(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            String nickname = nicknameTextField.getText();
            if (!nickname.isEmpty()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/tempGameView.fxml"));
                Parent root = fxmlLoader.load();
                GameController mainMenu = fxmlLoader.getController();
                //mainMenu.Setup(nickname);
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                //String url = Objects.requireNonNull(getClass().getResource("/css/menuPane.css")).toExternalForm();
                //scene.getStylesheets().add(url);
                stage.setScene(scene);
                stage.show();
            }
            nicknameTextField.clear();
        }
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedInitializationPhaseEvent'");
    }

    @Override
    public void handleJoinLobbyError(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleJoinLobbyError'");
    }

    @Override
    public void handleStartGameError(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleStartGameError'");
    }

    @Override
    public void handleCreateLobbyError(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCreateLobbyError'");
    }

    @Override
    public void handleLeaveLobbyError(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLeaveLobbyError'");
    }

    @Override
    public void handlePlayerTurnEvent(PlayerToken currentPlayer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayerTurnEvent'");
    }

    @Override
    public void handleLastRoundEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLastRoundEvent'");
    }

    @Override
    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDirectMessageEvent'");
    }

    @Override
    public void handleGroupMessageEvent(UserInfo sender, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGroupMessageEvent'");
    }
}
