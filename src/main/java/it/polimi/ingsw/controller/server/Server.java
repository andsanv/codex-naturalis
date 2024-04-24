package it.polimi.ingsw.controller.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The server is implemented using the Singleton pattern.
 * It handles users, lobbies and starting games.
 */
public enum Server {
    INSTANCE;

    Server() {
        this.lobbies = new HashMap<>();
        this.users = new HashSet<>();
    }

    Map<Integer, Lobby> lobbies;
    Set<User> users;

    /**
     * Adds the user to the lobby with the given id.
     * 
     * @param user    The user who wants to join the lobby
     * @param lobbyId The lobby id
     * @return True if joining was successfull, false if the lobby doesn't exist or
     *         if the user is already in the lobby.
     */
    boolean joinLobby(User user, int lobbyId) {
        synchronized (lobbies) {
            Lobby lobby = lobbies.get(lobbyId);

            return lobby != null && lobby.addUser(user);
        }
    }

    /**
     * Removes the user from the lobby with the given id.
     * If the user is the last one, the lobby gets deleted.
     * 
     * @param user    The user who wants to leave the lobby
     * @param lobbyId The lobby id
     * @return True if leaving the lobby was successfull, false if the lobby doesn't
     *         exist or if the user is already in the lobby.
     */
    boolean leaveLobby(User user, int lobbyId) {
        synchronized (lobbies) {
            Lobby lobby = lobbies.get(lobbyId);

            if (lobby == null)
                return false;

            if (!lobby.removeUser(user))
                lobbies.remove(lobbyId);

            return true;
        }
    }

    /**
     * Gives lobby informations.
     * 
     * @return The list of lobbies as LobbyInfo classes.
     */
    List<LobbyInfo> getLobbies() {
        synchronized (lobbies) {
            return lobbies.values().stream()
                    .map(lobby -> new LobbyInfo(lobby))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Starts the game by spawing the single game model and controller.
     * 
     * @param user    The user who tries to start the game
     * @param lobbyId The id of the lobby.
     * @return True if the game is started successfully, false if the game doesn't
     *         exist, if the user is not the lobby manager of if the game was
     *         already started.
     */
    boolean startGame(User user, int lobbyId) {
        synchronized (lobbies) {
            Lobby lobby = lobbies.get(lobbyId);

            if (lobby == null || user != lobby.getManager() || !lobby.startGame())
                return false;
        }

        // TODO spawn thread with gameflowmanager
        // TODO send to client connection informations for the started game

        return true;
    }

    /**
     * Signup method to create an user.
     */
    public UserInfo signup(String name, String password) {
        User user = new User(name);

        synchronized (users) {
            users.add(user);
        }

        return new UserInfo(user);
    }

    // TODO add a method to reconnect to the started game (connection info could be
    // added as an optional in the lobby class)
}
