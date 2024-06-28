package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.view.gui.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main GUI application.
 * Also acts as a "repository" of common objects between controllers.
 */
public class GUI extends Application {
    /**
     * The main controller of the application, that forwards every event to its "subController".
     */
    private MainController mainController;

    /**
     * Object that allows to connect to server, send commands and receive events.
     */
    public final AtomicReference<ConnectionHandler> connectionHandler = new AtomicReference<>(null);

    /**
     * As soon as the user connects to the server, he's assigned a UserInfo he will use to communicate with the server itself.
     */
    public final AtomicReference<UserInfo> selfUserInfo = new AtomicReference<>(null);

    /**
     * The stage of the GUI.
     */
    public final AtomicReference<Stage> stage = new AtomicReference<>(null);

    /**
     * List that will be used by the controllers to keep all available lobbies.
     */
    public final AtomicReference<List<LobbyInfo>> availableLobbies = new AtomicReference<>(new ArrayList<>());

    /**
     * To handle network commands, allowing the java-fx thread ot continue managing the gui.
     */
    public final AtomicReference<ExecutorService> executorService = new AtomicReference<>(Executors.newCachedThreadPool());

    /**
     * Represents the lobby in which the user currently is.
     */
    public final AtomicReference<LobbyInfo> currentLobby = new AtomicReference<>(null);

    /**
     * Entry point for the GUI application.
     *
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            stage.set(primaryStage);

            // load initial scene
            changeToConfigScene();

            primaryStage.setResizable(false);
            primaryStage.setTitle("Codex Naturalis");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main that allows the GUI to run.
     *
     * @param args args
     */
    public static void main(String[] args) {
        if (!Config.setUp(args)) {
            System.out.println("Invalid arguments");
            return;
        }
        launch(args);
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the initial configuration scene.
     */
    public void changeToConfigScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/configView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        ConfigController controller = loader.getController();

        if (mainController != null) mainController.setSubController(controller);
        else mainController = new MainController(this, controller);

        controller.initialize(this, mainController);

        System.out.println("[INFO] Changed to scene: Config");

        stage.get().setScene(new Scene(root));
        stage.get().centerOnScreen();
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the main menu scene.
     */
    public void changeToMenuScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/menuView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        MenuController controller = loader.getController();
        controller.initialize(this);

        Scene scene = new Scene(root);

        mainController.setSubController(controller);

        System.out.println("[INFO] Changed to scene: Menu");
        stage.get().setScene(scene);
        stage.get().centerOnScreen();
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the lobby scene.
     */
    public void changeToLobbyScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/lobbyView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        LobbyController controller = loader.getController();
        controller.initialize(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/lobbyView.css");

        mainController.setSubController(controller);

        System.out.println("[INFO] Changed to scene: Lobby");

        controller.updatePlayers();
        stage.get().setScene(scene);
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the lobbies list scene.
     */
    public void changeToLobbiesListScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/lobbiesListView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        LobbiesListController controller = loader.getController();
        controller.initialize(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/lobbiesListView.css");

        mainController.setSubController(controller);

        System.out.println("[INFO] Changed to scene: LobbiesList");

        stage.get().setScene(scene);
    }

    /**
     * Allows the user to show the first part of the game, where the token and the initial cards are chosen.
     */
    public void changeToSetupPhaseScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/setupPhaseView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        SetupPhaseController controller = loader.getController();
        controller.initialize(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/setupPhaseView.css");

        mainController.setSubController(controller);

        System.out.println("[INFO] Changed to scene: SetupPhase");

        stage.get().setScene(scene);
    }

    /**
     * Allows the user to enter the actual game scene.
     */
    public void changeToGameScene(SlimGameModel slimGameModel, Map<UserInfo, PlayerToken> userInfoToToken) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/gameView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        GameController controller = loader.getController();
        controller.initialize(this, slimGameModel, userInfoToToken);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/gameView.css");

        mainController.setSubController(controller);

        System.out.println("[INFO] Changed to scene: GameScene");

        stage.get().setScene(scene);
    }

    /**
     * Allows to have a single method from which send commands to the server.
     *
     * @param runnable the runnable to submit
     */
    public void submitToExecutorService(Runnable runnable) {
        this.executorService.get().submit(runnable);
    }
}
