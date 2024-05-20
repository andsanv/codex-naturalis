package it.polimi.ingsw.distributed.client;

import java.util.List;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.distributed.ConnectionInfo;
import it.polimi.ingsw.distributed.MainEventHandler;


public class Client implements MainEventHandler {

    @Override
    public void handleErrorMessage(String error) {
        System.out.println("\u001B[31m" + error + "\u001B[0m"); // Print error message in red
    }

    @Override
    public void handleLobbiesUpdate(List<LobbyInfo> lobbies) {
        System.out.println("\u001B[32mLobbies updated:\u001B[0m");
        for (LobbyInfo lobby : lobbies) {
            System.out.println("\u001B[33m" + lobby + "\u001B[0m"); // Print lobby name in yellow
        }
    }

    @Override
    public void handleReceivedConnection(ConnectionInfo connectionInfo) {
        System.out.println("\\u001B[33mReceived connection from: " + connectionInfo);
    }
    
}
