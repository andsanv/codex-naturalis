package it.polimi.ingsw.distributed.client.rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.client.RMIConnectionHandler;
import it.polimi.ingsw.distributed.commands.main.KeepAliveCommand;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.LoginEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.interfaces.GameServerActions;
import it.polimi.ingsw.distributed.interfaces.MainViewActions;
import it.polimi.ingsw.view.interfaces.MainEventHandler;

/**
 * This class is the RMI implementation of the MainViewActions interface.
 * It is used from the server to perform actions on the client main view.
 */
public class RMIMainView extends UnicastRemoteObject implements MainViewActions {

  /** 
   * This is the main event handler that will handle the main events received from the server.
   */
  private final MainEventHandler mainEventHandler;

  /** 
   * This conncetion handler will be used to handle the communication with the server.
   */
  private final RMIConnectionHandler connectionHandler;

  /**
   * This constructor creates a new RMIMainView.
   * 
   * @param mainEventHandler the main event handler to propagate the updates (events) to.
   * @throws RemoteException thrown when a communication error occurs
   */
  public RMIMainView(MainEventHandler mainEventHandler, RMIConnectionHandler rmiConnectionHandler) throws RemoteException {
    this.mainEventHandler = mainEventHandler;
    this.connectionHandler = rmiConnectionHandler;
  }

  /**
   * {@inheritDoc}
   * This method is used by the server to send an event to the client.
   */
  @Override
  public void trasmitEvent(MainEvent serverEvent) throws RemoteException, IOException {   
    if(serverEvent instanceof KeepAliveEvent) {
      connectionHandler.lastKeepAliveTime = System.currentTimeMillis();
      connectionHandler.sendToMainServer(new KeepAliveCommand(connectionHandler.userInterface.getUserInfo()));
    }
      
    serverEvent.execute(mainEventHandler);
  }

  /**
   * {@inheritDoc}
   * This method is used by the server to set the game server actions on the client.
   */
  @Override
  public void setGameServer(GameServerActions gameServer) throws RemoteException, IOException {
    connectionHandler.setGameServerActions(gameServer);
  }

  
}
