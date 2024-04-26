package it.polimi.ingsw.controller.server;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Game class represents a single game.
 * It contains the in-game connections to the clients, the controller and the
 * model.
 */
public class Game implements Runnable {
    private Lobby lobby;
    private Map<User, Boolean> isConnected;

    /**
     * When created, all clients are disconnected by default.
     * They must call joinGame() on the Server to join the match.
     * 
     * @param lobby lobby of the game
     */
    public Game(Lobby lobby) {
        this.lobby = lobby;
        isConnected = lobby.getUsers().stream()
                .collect(Collectors.toMap(Function.identity(), u -> false));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
