package it.polimi.ingsw.view.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.view.gui.controllers.MainMenu;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
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

public class Gui extends Application {



    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/gui.fxml"));
            Parent root = fxmlLoader.load(); // Load the FXML file

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
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
        super.stop();
        // shut down the executor service so that the application can exit
        executorService.shutdownNow();
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
