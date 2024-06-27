package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.view.gui.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller for the main menu view.
 * Allows to create a lobby or search for the already existing ones.
 */
public class MenuController extends Controller {
    @FXML private StackPane mainStackPane;
    @FXML private VBox buttonsPane;
    @FXML private Button createLobbyButton;
    @FXML private Button joinLobbyButton;
    @FXML private Button backButton;

    private AtomicReference<List<LobbyInfo>> activeLobbies = new AtomicReference<>(null);

    private final AtomicBoolean creatingLobby = new AtomicBoolean(false);

    public void initialize() {
        this.gui = new GUI();

        this.connectionHandler = null;
        this.selfUserInfo = gui.selfUserInfo;

        createLobbyButton.setOnAction(this::createLobby);
        joinLobbyButton.setOnAction(this::joinLobby);
        backButton.setOnAction(this::handleBackButtonAction);

        applyCss();
    }

    public void initialize(GUI gui) {
        this.gui = gui;
        this.connectionHandler = gui.connectionHandler;
        this.selfUserInfo = gui.selfUserInfo;
        this.activeLobbies = gui.availableLobbies;

        createLobbyButton.setOnAction(this::createLobby);
        joinLobbyButton.setOnAction(this::joinLobby);
        backButton.setOnAction(this::handleBackButtonAction);

        applyCss();
    }


    /* EVENT HANDLING */

    /**
     * Allows a user to create a lobby, in response to clicking the "createLobby" button.
     *
     * @param event the ActionEvent
     */
    public void createLobby(ActionEvent event) {
        creatingLobby.set(true);

        gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToMainServer(new CreateLobbyCommand(selfUserInfo.get()));
            System.out.println("[INFO] Submitted the CreateLobbyCommand");
        });

    }

    /**
     * Allows a user to join a lobby, in response to clicking the "joinLobby" button.
     *
     * @param event the ActionEvent
     */
    public void joinLobby(ActionEvent event) {
        gui.changeToLobbiesListScene();
    }

    /**
     * Allows the user to go back to Menu scene
     * @param event the ActionEvent event
     */
    public void handleBackButtonAction(ActionEvent event) {
        gui.changeToConfigScene();
    }

    /**
     * Handles the reception of the active lobbies.
     *
     * @param lobbies the active lobbies
     */
    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        this.activeLobbies.set(lobbies);

        lobbies.stream()
                .filter(lobby -> lobby.contains(selfUserInfo.get()))
                .findAny()
                .ifPresent(
                        lobby -> {
                            gui.changeToLobbyScene(lobby.users, lobby);
                        });
    }

    /**
     * Method called when an error occurred while creating a lobby.
     * The user could already be in another one.
     *
     * @param message the error message
     */
    @Override
    public void handleCreateLobbyError(String message) {
        creatingLobby.set(false);

        showPopup(message);
    }

    /**
     * This method handles the reception of generic server error
     *
     * @param error description of the error
     */
    public void handleServerError(String error) {
        showPopup(error);
    }

    /* HELPERS */

    public void applyCss() {
        mainStackPane.getStyleClass().add("main-pane");
        backButton.getStyleClass().add("button-back");
        joinLobbyButton.getStyleClass().add("button-central");
        createLobbyButton.getStyleClass().add("button-central");
        buttonsPane.getStyleClass().add("buttons-pane");
    }

    /**
     * Allows to show a message received by the server.
     *
     * @param message the message received
     */
    public void showPopup(String message) {
        System.out.println("[ERROR] " + message);
    }
}
