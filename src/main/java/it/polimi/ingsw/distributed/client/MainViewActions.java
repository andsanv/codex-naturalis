package it.polimi.ingsw.distributed.client;

import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the client main view.
 */
public interface MainViewActions extends Remote, Serializable {
  /**
   * Sends a server event to the client
   *
   * @param serverEvent the event that the client will receive
   * @throws RemoteException
   */
  public void receiveEvent(MainEvent serverEvent) throws IOException;

  /**
   * This method is used to set the created game server once a game is started. (rmi)
   * @param gameServer the game server instance
   * @throws IOException
   */
  public void setGameServer(GameServerActions gameServer) throws IOException;
}
