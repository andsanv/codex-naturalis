package distributed.interfaces;

import distributed.events.main.MainEvent;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the client main
 * view.
 */
public interface MainViewActions extends Remote, Serializable {
  /**
   * This method will send a main event to the client
   *
   * @param serverEvent the event to be sent
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  public void trasmitEvent(MainEvent serverEvent) throws RemoteException, IOException;

  /**
   * This method is used to set the created game server once a game is started.
   * (rmi)
   * 
   * @param gameServer the game server instance
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  public void setGameServer(GameServerActions gameServer) throws RemoteException, IOException;
}
