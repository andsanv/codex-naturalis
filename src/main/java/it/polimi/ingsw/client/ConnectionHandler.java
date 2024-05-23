package it.polimi.ingsw.client;

import it.polimi.ingsw.distributed.commands.GameCommand;
import it.polimi.ingsw.distributed.commands.ServerCommand;

/**
 * ConnectionHandler can be used as a generic connection and hides the underlying socket or RMI implementation. 
 */
public abstract class ConnectionHandler {
    private boolean connectedToGame;

    public abstract boolean sendToMainServer(ServerCommand serverCommand);

    public abstract boolean sendToGameServer(GameCommand gameCommand);

    public abstract boolean reconnect();
}