package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.distributed.ConnectionInfo;
import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.commands.ServerCommand;

public class GameConnectionEvent extends ServerCommand {

    private final ConnectionInfo connectionInfo;
    private final MainEventHandler mainEventHandler;

    @Override
    public void execute() {
        mainEventHandler.handleReceivedConnection(connectionInfo);
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public MainEventHandler getMainEventHandler() {
        return mainEventHandler;
    }

    public GameConnectionEvent(ConnectionInfo connectionInfo, MainEventHandler mainEventHandler) {
        this.connectionInfo = connectionInfo;
        this.mainEventHandler = mainEventHandler;
    }
    
}
