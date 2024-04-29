package it.polimi.ingsw.controller.server;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;

/**
 * The Game class represents a single game.
 * It contains the in-game connections to the clients, the controller and the
 * model.
 */
public class Game {
    private Lobby lobby;
    private Map<User, Boolean> isConnected;

    private GameFlowManager game;

    /**
     * When created, all clients are disconnected by default.
     * The method joinGame() of Server must be called to join the match.
     * 
     * @param lobby lobby of the game
     */
    public Game(Lobby lobby) {
        this.lobby = lobby;
        isConnected = lobby.getUsers().stream()
                .collect(Collectors.toMap(Function.identity(), u -> false));
    }
}
