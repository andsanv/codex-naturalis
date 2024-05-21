package it.polimi.ingsw.distributed.events.main;

import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.distributed.MainEventHandler;

public class LobbiesEvent extends MainEvent {
    private final List<LobbyInfo> lobbies;

    public LobbiesEvent(List<LobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }

    public List<LobbyInfo> getLobbies() {
        return lobbies;
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleLobbiesEvent(lobbies);
    }
}