package it.polimi.ingsw.distributed.client.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;

public class RMIMainView extends UnicastRemoteObject implements MainViewActions {
  private UserInfo userInfo;
  private final MainEventHandler mainEventHandler;
  private RMIConnectionHandler connectionHandler;

  // private final Object printLock;

  public RMIMainView(MainEventHandler mainEventHandler) throws RemoteException {
    // this.printLock = printLock;
    this.userInfo = new UserInfo(new User("test"));
    this.mainEventHandler = mainEventHandler;
  }

  @Override
  public void receiveEvent(MainEvent serverEvent) throws RemoteException {
    serverEvent.execute(mainEventHandler);
  }

  @Override
  public void setGameServer(GameServerActions gameServer) throws RemoteException {
    this.connectionHandler.setGameServerActions(gameServer);
  }

  @Override
  public void update(GameEvent event) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }
}
