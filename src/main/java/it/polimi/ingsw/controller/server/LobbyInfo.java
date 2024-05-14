package it.polimi.ingsw.controller.server;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LobbyInfo contains all the info about a lobby at the time it was created.
 * The class can be safely shared since it is final and doesn't hold any
 * references.
 */
public final class LobbyInfo implements Serializable {
    public final int id;
    public final UserInfo manager;
    public final List<UserInfo> users;
    public final boolean gameStarted;

    public LobbyInfo(Lobby lobby) {
        this.id = lobby.id;
        this.manager = new UserInfo(lobby.getManager());
        this.users = lobby.getUsers().stream()
                .map(user -> new UserInfo(user))
                .collect(Collectors.toList());
        this.gameStarted = lobby.gameStarted;
    }

    @Override
    public String toString() {
        return "id: " + id + ", manager: " + manager + ", users" + users.toString() + ", gameStarted: " + gameStarted;
    }
}
