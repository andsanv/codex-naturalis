package distributed.interfaces;

import distributed.events.game.GameEvent;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the client's game view.
 */
public interface GameViewActions extends Remote {
  /**
   * This method will send a GameEvent to the client.
   * 
   * @param event the event to be sent
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException thrown when an I/O error occurs
   */
  void transmitEvent(GameEvent event) throws RemoteException, IOException;
}
