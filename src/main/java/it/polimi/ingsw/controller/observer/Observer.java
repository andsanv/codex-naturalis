package it.polimi.ingsw.controller.observer;

import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.game.GameEvent;

/** An observer of GameEvents */
public interface Observer {
  /**
   * This method handles received GameEvents
   *
   * @param event the received event
   * @throws RemoteException 
   */
  void update(GameEvent event) throws RemoteException;
}
