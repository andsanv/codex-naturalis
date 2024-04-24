package it.polimi.ingsw.controller.server;

import java.util.ArrayList;
import java.util.List;

/**
 * The lobby contains up to four users.
 * It has a manager (an user) and an unique id (incremental from 0).
 * The class is synchronized.
 */
public class Lobby {
    private static int nextId = 0;

    public final int id;
    private User manager;
    private List<User> users;
    boolean gameStarted;

    /**
     * @return A copy of the list of users connected to the lobby
     */
    public synchronized List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * @return The manager of the lobby
     */
    public synchronized User getManager() {
        return manager;
    }

    /**
     * Constructs a Lobby with an unique id (incremental from 0).
     * 
     * @param creator The user who creates the lobby.
     */
    public Lobby(User creator) {
        id = nextId;
        Lobby.nextId++;

        users = new ArrayList<>();
        users.add(manager);

        manager = creator;

        gameStarted = false;
    }

    /**
     * Tries to add an user to the lobby.
     * This method fails if the lobby is full or if the same user is already
     * connected.
     *
     * @param user The user to add to the lobby
     * 
     * @return true if successfull, false otherwise
     */
    public synchronized boolean addUser(User user) {
        if (users.size() == 4)
            return false;

        if (users.contains(user))
            return false;

        users.add(user);
        return true;
    }

    /**
     * Removes the given user from the lobby, if he's present.
     * If the lobby manager is removed, the next "oldest" user in the lobby takes
     * his place.
     * If there are no users left after removing the given one, this method returns
     * false.
     * 
     * @param user user to remove from the lobby
     * 
     * @return true if there are still users left in the lobby, false otherwise
     */
    public synchronized boolean removeUser(User user) {
        users.remove(user);

        if (manager == user)
            manager = users.get(0);

        return users.size() > 0;
    }

    /**
     * Must be called when starting a game.
     * After this method is called, the gameStarted attribute will be equal to true.
     * 
     * @return false if the game was already started, true otherwise
     */
    public synchronized boolean startGame() {
        if (gameStarted)
            return false;

        gameStarted = true;
        return true;
    }
}