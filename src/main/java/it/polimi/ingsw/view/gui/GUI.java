package it.polimi.ingsw.view.gui;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.gui.controllers.Controller;
import it.polimi.ingsw.view.gui.controllers.GameController;
import it.polimi.ingsw.view.gui.controllers.TempGameController;
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

    public Pair<Integer, Integer> screenResolution;
    public double screenRatio;

    public Controller currentController;

    public static void main(String[] args) {
        launch(args);
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

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/tempGameView.fxml"));
            Parent root = fxmlLoader.load();

            TempGameController controller = fxmlLoader.getController();
            controller.initialize(this);
            Scene scene = new Scene(root);
            String url = Objects.requireNonNull(getClass().getResource("/css/gameView.css")).toExternalForm();
            scene.getStylesheets().add(url);

            // primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Codex Naturalis");
            primaryStage.show();


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
}
