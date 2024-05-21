package it.polimi.ingsw.distributed;

import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;

public interface MainEventHandler {
    public void handleServerError(String error);

    public void handleLobbiesEvent(List<LobbyInfo> lobbies);

    public void handleReceivedConnection(String rmiConnectionInfo, String socketConnectionInfo);
}
