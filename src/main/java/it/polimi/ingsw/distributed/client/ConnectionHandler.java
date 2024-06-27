package it.polimi.ingsw.distributed.client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.view.UI;

/**
 * This abstract class is the interface for the connection handler. Multiple
 * implementations can be used.
 * The connection handler helps the client to communicate with the server.
 */
public abstract class ConnectionHandler {

  public final AtomicBoolean isConnected = new AtomicBoolean(false);

  /**
   * This represents the user interface of the client.
   */
  public final UI userInterface;

  /**
   * This field is used to check if the next KeepAliveEvent has come after the
   * timeout.
   */
  public long lastKeepAliveTime = 0L;

  /**
   * This field represents the maximum span between two KeepAliveEvent, if the
   * time is exceeded the connection is considered lost.
   */
  public static long MILLISEC_TIME_OUT = 5000L;

  public ConnectionHandler(UI userInterface) {
    this.userInterface = userInterface;
  }

  /**
   * This method is used to send a main command to the main server.
   * 
   * @param serverCommand the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  public abstract boolean sendToMainServer(MainCommand serverCommand);

  /**
   * This method is used to send a game command to the game server.
   * 
   * @param gameCommand the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  public abstract boolean sendToGameServer(GameCommand gameCommand);

  /**
   * This method is used to connect the client to the server.
   * 
   * @param connectionCommand the command containing the user information.
   * @return true if the connection was successful, false otherwise.
   */
  public abstract boolean connect(ConnectionCommand connectionCommand);

  /**
   * This method must be called when the client wants to try to reconnect to the
   * server.
   * 
   * @return true if the reconnection was successful, false otherwise.
   */
  public abstract boolean reconnect();

  /**
   * This method is used to check the connection.
   */
  protected abstract void checkConnection();

}
