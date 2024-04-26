package it.polimi.ingsw.distributed.network;

import java.util.List;

public interface LobbyHandler {
    Lobby createLobby();

    void joinLobby(int lobbyId);

    void startGame();

    List<Lobby> getAvailableLobbies();

    void reconnectToGame(int lobbyId);

    User signUp(String username, String password);

    boolean login(String password);

}


class Lobby {
    private int id;
    private User creator;
    private List<User> users;
    boolean ongoing;
}

class User {
    private String name;
    private int id;

    @Override
    public String toString() {
        return name + "#" + id;
    }
}