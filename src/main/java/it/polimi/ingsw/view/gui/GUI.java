package it.polimi.ingsw.view.gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.gui.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class GUI extends Application {
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public Controller currentController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/configView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Codex Naturalis");
            primaryStage.show();

            initialize(); // Call initialize after the FXML is loaded

            ServerEventHandlerTask serverEventHandlerTask = new ServerEventHandlerTask();
            executorService.submit(serverEventHandlerTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        // shut down the executor service so that the application can exit
        executorService.shutdownNow();

        super.stop();
    }

    @FXML
    public void initialize() {
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
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/mainMenu.fxml"));
                Parent root = fxmlLoader.load();
                MainMenu mainMenu = fxmlLoader.getController();
                mainMenu.Setup(nickname);
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                String url = Objects.requireNonNull(getClass().getResource("/css/menuPane.css")).toExternalForm();
                scene.getStylesheets().add(url);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnterKeyPressed(KeyEvent event) {
        try {
            if (event.getCode() == KeyCode.ENTER) {
                String nickname = nicknameTextField.getText();
                if (!nickname.isEmpty()) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/mainMenu.fxml"));
                    Parent root = fxmlLoader.load();
                    MainMenu mainMenu = fxmlLoader.getController();
                    mainMenu.Setup(nickname);
                    Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    String url = Objects.requireNonNull(getClass().getResource("/css/menuPane.css")).toExternalForm();
                    scene.getStylesheets().add(url);
                    stage.setScene(scene);
                    stage.show();
                }
                nicknameTextField.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
