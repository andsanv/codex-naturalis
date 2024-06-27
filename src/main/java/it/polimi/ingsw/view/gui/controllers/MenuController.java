package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.view.gui.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for the main menu view.
 * Allows to create a lobby or search for the already existing ones.
 */
public class MenuController extends Controller {
    public GUI gui;

    @FXML private StackPane mainStackPane;

    @FXML private Button createLobbyButton;
    @FXML private Button joinLobbyButton;
    @FXML private Button backButton;

    private List<LobbyInfo> activeLobbies;

    private final AtomicBoolean creatingLobby = new AtomicBoolean(false);

    public void initialize() {
        this.gui = new GUI();
        this.connectionHandler = null;

        createLobbyButton.setOnAction(this::createLobby);
        joinLobbyButton.setOnAction(this::joinLobby);
        backButton.setOnAction(this::handleBackButtonAction);

        applyCss();
    }

    public void initialize(GUI gui) {
        this.gui = gui;

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

        executorService.submit(() -> {
            gui.connectionHandler.sendToMainServer(new CreateLobbyCommand(gui.selfUserInfo.get()));
        });
    }

    /**
     * Allows a user to join a lobby, in response to clicking the "joinLobby" button.
     *
     * @param event the ActionEvent
     */
    public void joinLobby(ActionEvent event) {
        gui.changeToLobbiesListScene(activeLobbies);
    }

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
        this.activeLobbies = lobbies;

        lobbies.stream()
                .filter(lobby -> lobby.contains(gui.selfUserInfo.get()))
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
    }

    /**
     * Allows to show a message received by the server.
     *
     * @param message the message received
     */
    public void showPopup(String message) {}
}
