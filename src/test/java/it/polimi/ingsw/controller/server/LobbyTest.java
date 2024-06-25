package it.polimi.ingsw.controller.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.controller.usermanagement.Lobby;
import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;

public class LobbyTest {
    @Test
    void testFullLobby() {
        User creator = new User("creator");
        Lobby lobby = new Lobby(creator);

        assertTrue(lobby.contains(creator));
        assertTrue(lobby.contains(new UserInfo(creator)));

        assertFalse(lobby.startGame());

        User pippo = new User("pippo");

        assertFalse(lobby.contains(pippo));
        assertFalse(lobby.contains(new UserInfo(pippo)));

        assertFalse(lobby.addUser(creator));

        assertTrue(lobby.addUser(pippo));

        assertTrue(lobby.contains(pippo));

        User pluto = new User("pluto");

        assertTrue(lobby.addUser(pluto));

        assertTrue(lobby.contains(pluto));

        assertFalse(lobby.isFull());

        assertTrue(lobby.removeUser(pluto));

        assertTrue(lobby.addUser(pluto));

        assertTrue(lobby.contains(pluto));

        User paperino = new User("Paperino");

        assertTrue(lobby.addUser(paperino));

        assertTrue(lobby.contains(paperino));

        assertTrue(lobby.isFull());

        assertTrue(lobby.startGame());

        assertFalse(lobby.removeUser(paperino));
    }

    @Test
    void testLobbyInfo() {
        User leader = new User("leader");
        Lobby lobby = new Lobby(leader);

        LobbyInfo lobbyInfo = lobby.toLobbyInfo();

        assertTrue(Lobby.getLobbies().contains(lobbyInfo));

        assertTrue(lobby.getUsers().contains(leader));
    }
}