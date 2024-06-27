package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.view.gui.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
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
    public MainController mainController;

    /**
     * Object that allows to connect to server, send commands and receive events.
     */
    public ConnectionHandler connectionHandler = null;

    /**
     * As soon as the user connects to the server, he's assigned a UserInfo he will use to communicate with the server itself.
     */
    public AtomicReference<UserInfo> selfUserInfo = new AtomicReference<>(null);

    /**
     * The stage of the GUI.
     */
    public Stage stage = null;

    /**
     * To handle network commands, allowing the java-fx thread ot continue managing the gui.
     */
    public final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Entry point for the GUI application.
     *
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            this.stage = primaryStage;

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
     * main that allows the GUI to run.
     *
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the initial configuration scene.
     */
    public void changeToConfigScene() {
        if (mainController != null) this.mainController.subController = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/configView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        ConfigController controller = loader.getController();
        controller.initialize(this);

        if (mainController != null) mainController.setSubController(controller);
        else mainController = new MainController(this, controller);

        System.out.println("main controller: " + mainController);

        System.out.println("root = " + root);
        System.out.println("scene = " + root.getScene());

        stage.setScene(new Scene(root));
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the main menu scene.
     */
    public void changeToMenuScene() {
        this.mainController.subController = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/menuView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        MenuController controller = loader.getController();
        controller.initialize(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/menuView.css");

        mainController.setSubController(controller);

        stage.setScene(scene);
    }

    /**
     * Allows the GUI application to update the scene, transitioning to the lobby scene.
     */
    public void changeToLobbyScene(List<UserInfo> users, LobbyInfo lobby) {
        this.mainController.subController = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/lobbyView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        LobbyController controller = loader.getController();
        controller.initialize(this, lobby);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/lobbyView.css");

        mainController.setSubController(controller);

        stage.setScene(scene);
    }

    /**
     * Allows to switch from menu to the lobbies list
     *
     * @param activeLobbies current active lobbies
     */
    public void changeToLobbiesListScene(List<LobbyInfo> activeLobbies) {
        this.mainController.subController = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gui/lobbiesListView.fxml"));
        Parent root = null;
        try { root = loader.load(); } catch (Exception e) { e.printStackTrace(); }

        LobbiesListController controller = loader.getController();
        controller.initialize(this, activeLobbies);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/lobbiesListView.css");

        mainController.setSubController(controller);

        stage.setScene(scene);
    }

    public UserInfo getUserInfo() {
        return this.selfUserInfo.get();
    }
}
