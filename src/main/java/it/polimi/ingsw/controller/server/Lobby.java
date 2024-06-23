package it.polimi.ingsw.controller.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.polimi.ingsw.controller.GameFlowManager;

/**
 * The lobby contains up to four users. It has a manager (an user) and an unique
 * id (incremental from 1). The class is synchronized.
 */
public class Lobby {
    /**
     * A set that contains all active lobbies (lobbies that haven't been deleted
     * yet).
     */
    private static final Set<Lobby> lobbies = new HashSet<>();

    /**
     * Id of the next lobby
     */
    private static int nextId = 1;

    /**
     * Unique id of the lobby
     */
    public final int id;

    /**
     * The player that can start the game
     */
    private User manager;

    /**
     * The list of users in the lobby
     */
    private final List<User> users;

    /**
     * A boolean that is true if the game was started
     */
    private boolean gameStarted;

    /**
     * The lobby's GameFlowManager that handles the on-going game.
     */
    private GameFlowManager gameFlowManager;

    /**
     * Checks if the lobby is full.
     * 
     * @return true if full, false otherwise
     */
    public synchronized boolean isFull() {
        return users.size() == 4;
    }

    /**
     * Checks if the given user (as UserInfo) is in the lobby.
     * 
     * @param userInfo the UserInfo of the user
     * @return true if the user is in the lobby, false otherwise
     */
    public synchronized boolean contains(UserInfo userInfo) {
        return users.stream()
                .anyMatch(u -> u.equals(userInfo));
    }

    /**
     * Checks if the given user (as User) is in the lobby.
     * 
     * @param user the user
     * @return true if the user is in the lobby, false otherwise
     */
    public synchronized boolean contains(User user) {
        return users.stream()
                .anyMatch(u -> u.equals(user));
    }

    /**
     * Checks if the lobby is in game.
     * 
     * @return true if full, false otherwise
     */
    public synchronized boolean gameStarted() {
        return gameStarted;
    }

    /**
     * @return A copy of the list of users connected to the lobby
     */
    public synchronized List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * @return The manager of the lobby
     */
    public synchronized UserInfo getManager() {
        return new UserInfo(manager);
    }

    /**
     * Constructs a Lobby with an unique id (incremental from 0).
     * The creator must be not null.
     *
     * @param creator The user who creates the lobby.
     */
    public Lobby(User creator) {
        synchronized (Lobby.class) {
            id = nextId;
            lobbies.add(this);

            nextId++;
        }

        users = new ArrayList<>();
        users.add(creator);

        manager = creator;

        gameStarted = false;
    }

    /**
     * Tries to insert an user to the lobby. This method fails if the lobby is full
     * or if the user is already in a lobby.
     *
     * @param user The user to insert to the lobby
     * @return true if successfull, false otherwise
     */
    public synchronized boolean addUser(User user) {
        if (this.isFull() || Lobby.anyLobbyContains(user))
            return false;

        users.add(user);
        return true;
    }

    /**
     * Removes the given user from the lobby, if he's present. If the lobby manager
     * is removed, the next "oldest" user in the lobby takes his place. If there are
     * no users left after removing the given one, this method deletes the lobby and
     * returns false. The method removes the user only if the game hasn't been
     * started.
     *
     * @param user user to remove from the lobby
     * @return true if removed, false if the user wasn't in the lobby or the game
     *         has already been started.
     */
    public synchronized boolean removeUser(User user) {
        if (gameStarted || !users.remove(user))
            return false;

        if (users.isEmpty()) {
            deleteLobby(this.id);
            return true;
        }

        if (manager == user)
            manager = users.get(0);

        return true;
    }

    /**
     * Must be called when starting a game. After this method is called, the
     * gameStarted attribute will be equal to true.
     *
     * @return false if the game was already started or there are not enough players, true otherwise
     */
    public synchronized boolean startGame() {
        if (gameStarted || users.size() < 2)
            return false;

        gameStarted = true;
        return true;
    }

    /**
     * Returns the list of active lobbies.
     *
     * @return The list of active lobbies as LobbyInfo.
     * 
     * @see LobbyInfo
     */
    public static List<LobbyInfo> getLobbies() {
        synchronized (Lobby.class) {
            return lobbies.stream()
                    .map(lobby -> new LobbyInfo(lobby))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Finds the lobby with the given id.
     * 
     * @param lobbyId the lobby id
     * @return the Lobby corresponding to the given id or null if not found
     */
    public static Lobby getLobby(int lobbyId) {
        synchronized (Lobby.class) {
            return lobbies.stream()
                    .filter(l -> l.id == lobbyId)
                    .findAny()
                    .orElse(null);
        }
    }

    /**
     * Finds the lobby with the given user.
     * 
     * @param userInfo the user in the lobby
     * @return the Lobby corresponding to the given id
     */
    public static Lobby getLobby(UserInfo userInfo) {
        synchronized (Lobby.class) {
            return lobbies.stream()
                    .filter(l -> l.users.stream()
                            .anyMatch(u -> u.equals(userInfo)))
                    .findAny()
                    .orElse(null);
        }
    }

    /**
     * Deletes a lobby.
     * Must be called when the match ends.
     * 
     * @param lobbyId the id of the lobby
     * 
     * @return true if the lobby existed and was deleted, false if not found
     */
    public static boolean deleteLobby(int lobbyId) {
        synchronized (Lobby.class) {
            return lobbies.remove(getLobby(lobbyId));
        }
    }

    /**
     * Checks if the user is in one of the active lobbies.
     * 
     * @param userInfo the UserInfo of the user
     * @return true if the user is in a lobby, false otherwise
     */
    public static boolean anyLobbyContains(UserInfo userInfo) {
        synchronized (Lobby.class) {
            return lobbies.stream()
                    .anyMatch(l -> l.contains(userInfo));
        }
    }

    /**
     * Creates a LobbyInfo of the lobby on which it is called.
     * 
     * @return the LobbyInfo
     */
    public synchronized LobbyInfo toLobbyInfo() {
        return new LobbyInfo(this);
    }

    /**
     * Checks if the user is in one of the active lobbies.
     * 
     * @param userInfo the user
     * @return true if the user is in a lobby, false otherwise
     */
    public static boolean anyLobbyContains(User user) {
        synchronized (Lobby.class) {
            return lobbies.stream()
                    .anyMatch(l -> l.contains(user));
        }
    }

    /**
     * Removes the user from the lobby he is in.
     * If the game has already been started no action is done.
     * 
     * @param userInfo the UserInfo of the player
     * @return true if removed from any lobby, false otherwise
     */
    public static boolean removeUserIfGameNotStarted(UserInfo userInfo) {
        synchronized (Lobby.class) {
            return lobbies.stream().filter(l -> !l.gameStarted && l.contains(userInfo)).findAny()
                    .map(l -> l.removeUser(User.userInfoToUser(userInfo))).orElse(false);
        }
    }

    /**
     * Getter of the GameFlowManager of the lobby
     * 
     * @return the GameFlowManager reference if it was set, null otherwise
     */
    public synchronized GameFlowManager getGameFlowManager() {
        return gameFlowManager;
    }

    /**
     * Setter of the GameFlowManager of the lobby
     * 
     * @return the GameFlowManager reference if it was set, null otherwise
     */
    public synchronized void setGameFlowManager(GameFlowManager gameFlowManager) {
        this.gameFlowManager = gameFlowManager;
    }

    @Override
    public boolean equals(Object obj) {
        Lobby other = (Lobby) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
