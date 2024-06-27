package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller for the main menu view.
 * Allows to create a lobby or search for the already existing ones.
 */
public class MenuController extends Controller {
    public GUI gui;

    @FXML private Button createGameButton;
    @FXML private Button joinGameButton;
    @FXML private Button backButton;

    private List<LobbyInfo> activeLobbies;

    private final AtomicInteger lobbyId = new AtomicInteger(-1);

    private final AtomicBoolean creatingLobby = new AtomicBoolean(false);
    private final AtomicBoolean inGame = new AtomicBoolean(false);

    public void initialize(GUI gui) {
        this.gui = gui;

        createGameButton.setOnMouseClicked(this::handleCreateGame);
    }


    /* EVENT HANDLING */

    /**
     * Allows a user to create a game, in response to clicking the "createGame" button.
     *
     * @param mouseEvent the MouseEvent
     */
    public void handleCreateGame(MouseEvent mouseEvent) {
        creatingLobby.set(true);
        gui.connectionHandler.sendToMainServer(new CreateLobbyCommand(gui.selfUserInfo));
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
                .filter(lobby -> lobby.contains(gui.selfUserInfo))
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

    /**
     * Allows to show a message received by the server.
     *
     * @param message the message received
     */
    public void showPopup(String message) {}
}
