package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import javafx.application.Platform;

import java.util.List;

/**
 * Controller of the join lobby menu.
 * Allows the user to join a lobby chosen from a list.
 */
public class JoinLobbyController extends Controller {
    List<LobbyInfo> lobbies;

    /**
     * Initializes the list of lobbies.
     *
     * @param lobbies list of open lobbies in the server
     */
    public void initialize(List<LobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }


    /* GUI */

    /**
     * This method handles the request from the user of updating the list of lobbies.
     *
     */
    public void updateLobbies() {

    }


    /* NETWORK */

    /**
     * Allows to join a lobby.
     *
     * @param lobbyIndex index of the lobby in the list of lobbies
     */
    public void joinLobby(Integer lobbyIndex) {
        Platform.runLater(() -> {

        });
    }

    /**
     * Handles the reception of the updated list of lobbies.
     *
     * @param lobbies list of lobbies
     */
    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        Platform.runLater(() -> {
            this.lobbies = lobbies;
        });
    }
}
