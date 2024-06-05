package it.polimi.ingsw.distributed.client;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MainViewActions extends Remote, Serializable, Observer {
  /**
   * Sends a server update to the client
   *
   * @param serverEvent the event that the client will receive
   * @throws RemoteException
   */
  public void receiveEvent(MainEvent serverEvent) throws RemoteException;

  public void setGameServer(GameServerActions gameServer) throws RemoteException;
}
