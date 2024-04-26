package it.polimi.ingsw.controller.server;

import java.util.List;


public interface ConnectionActions {
    boolean joinLobby(User user, int lobbyId);

    boolean leaveLobby(User user, int lobbyId);

    List<LobbyInfo> getLobbies();

    boolean startGame(User user, int lobbyId);

    boolean joinGame(User user, int lobbyId);

    public UserInfo signup(String name, String password);
}
