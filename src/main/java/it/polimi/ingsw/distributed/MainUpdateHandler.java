package it.polimi.ingsw.distributed;

import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;

public interface MainUpdateHandler {

    public void handleErrorMessage(String error);

    public void handleLobbiesUpdate(List<LobbyInfo> lobbies);
}
