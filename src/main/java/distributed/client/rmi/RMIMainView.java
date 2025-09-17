package distributed.client.rmi;

import distributed.client.RMIConnectionHandler;
import distributed.commands.main.KeepAliveCommand;
import distributed.events.main.KeepAliveEvent;
import distributed.events.main.MainEvent;
import distributed.interfaces.GameServerActions;
import distributed.interfaces.MainViewActions;
import view.interfaces.MainEventHandler;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
   * This executor service is used to execute asynchronously the events received from the server.
   */
   private final ExecutorService executorService = Executors.newCachedThreadPool();

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
      
    executorService.submit(() -> serverEvent.execute(mainEventHandler));
  }

  /**
   * {@inheritDoc}
   * This method is used by the server to set the game server actions on the client.
   */
  @Override
  public void setGameServer(GameServerActions gameServer) throws RemoteException, IOException {
    executorService.submit(() -> connectionHandler.setGameServerActions(gameServer));
  }

  
}
