package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

public class GameConnectionEvent extends MainEvent {

    private final String rmiConnectionInfo;
    private final int socketConnectionPort;

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReceivedConnection(rmiConnectionInfo, socketConnectionPort);
    }

    public GameConnectionEvent(String rmiConnectionInfo, int socketConnectionPort) {
        this.rmiConnectionInfo = rmiConnectionInfo;
        this.socketConnectionPort = socketConnectionPort;
    }
}
