package it.polimi.ingsw.distributed.client;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.GameEvent;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the client game view.
 */
public interface GameViewActions extends Remote, Observer {
  /**
   * Receives a game event.
   * @param event the sent event
   * @throws RemoteException
   * @throws IOException 
   */
  void receiveEvent(GameEvent event) throws RemoteException, IOException;
}
