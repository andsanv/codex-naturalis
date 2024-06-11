package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.view.UI;

/**
 * ConnectionHandler can be used as a generic connection and hides the underlying socket or RMI
 * implementation.
 */
public abstract class ConnectionHandler {
  private boolean connectedToGame;
  protected final UI userInterface;

  public ConnectionHandler(UI userInterface) {
    this.userInterface = userInterface;
    this.connectedToGame = false;
  }

  public abstract boolean sendToMainServer(MainCommand serverCommand);

  public abstract boolean sendToGameServer(GameCommand gameCommand);

  public abstract boolean reconnect();

}
