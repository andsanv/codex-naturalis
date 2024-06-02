package it.polimi.ingsw.client;

import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;

/**
 * ConnectionHandler can be used as a generic connection and hides the underlying socket or RMI implementation. 
 */
public abstract class ConnectionHandler {
    private boolean connectedToGame;
    private final UI userInterface;

    public ConnectionHandler(UI userInterface) {
        this.userInterface = userInterface;
        this.connectedToGame = false;
    }

    public abstract boolean sendToMainServer(MainCommand serverCommand);

    public abstract boolean sendToGameServer(GameCommand gameCommand);

    public abstract boolean sendToServer(Command command);

    public abstract boolean reconnect();
}