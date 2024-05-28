package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

public class GameConnectionEvent extends MainEvent {

    public final String rmiConnectionInfo;
    public final int socketConnectionPort;

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReceivedConnection();
    }

    public GameConnectionEvent(String rmiConnectionInfo, int socketConnectionPort) {
        this.rmiConnectionInfo = rmiConnectionInfo;
        this.socketConnectionPort = socketConnectionPort;
    }
}
