package it.polimi.ingsw.distributed.events.main;

import java.util.List;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.distributed.MainEventHandler;

public class LobbiesEvent extends MainEvent {
    private final List<LobbyInfo> lobbies;

    public LobbiesEvent(List<LobbyInfo> lobbies) {
        System.out.println("Lobbies event created");
        this.lobbies = lobbies;
    }

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleLobbiesEvent(lobbies);
    }
}