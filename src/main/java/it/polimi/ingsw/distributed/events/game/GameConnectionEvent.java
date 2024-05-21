package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.events.main.MainEvent;

public class GameConnectionEvent extends MainEvent {

    private final String rmiConnectionInfo;
    private final String socketConnectionInfo;

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReceivedConnection(rmiConnectionInfo, socketConnectionInfo);
    }

    public GameConnectionEvent(String rmiConnectionInfo, String socketConnectionInfo) {
        this.rmiConnectionInfo = rmiConnectionInfo;
        this.socketConnectionInfo = socketConnectionInfo;
    }
}
