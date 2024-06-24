package it.polimi.ingsw.view.connection;

import java.util.concurrent.atomic.AtomicBoolean;

import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.view.UI;

/**
 * This abstract class is the interface for the connection handler. Multiple implementations can be used.
 * The connection handler helps the client to communicate with the server.
 */
public abstract class ConnectionHandler {

  public final AtomicBoolean isConnected = new AtomicBoolean(false);
  
  /**
   * This represents the user interface of the client.
   */
  protected final UI userInterface;

  public ConnectionHandler(UI userInterface) {
    this.userInterface = userInterface;
  }

  /**
   * This method is used to send a main command to the main server.
   * @param serverCommand the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  public abstract boolean sendToMainServer(MainCommand serverCommand);

  /**
   * This method is used to send a game command to the game server.
   * @param gameCommand the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  public abstract boolean sendToGameServer(GameCommand gameCommand);

  /**
   * This method must be called when the client wants to try to reconnect to the server.
   * @return true if the reconnection was successful, false otherwise
   */
  public abstract boolean reconnect();

}
