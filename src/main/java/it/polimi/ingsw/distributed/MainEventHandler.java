package it.polimi.ingsw.distributed;

import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;

public interface MainEventHandler {
    public void handleUserInfo(UserInfo userInfo);

    public void handleServerError(String error);

    public void handleLobbiesEvent(List<LobbyInfo> lobbies);

    public void handleReceivedConnection();

}
