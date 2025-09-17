package distributed.client.rmi;

import distributed.events.game.GameEvent;
import distributed.interfaces.GameViewActions;
import view.interfaces.GameEventHandler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the RMI implementation of the GameViewActions interface.
 * It is used from the server to perform actions on the client game view.
 */
public class RMIGameView extends UnicastRemoteObject implements GameViewActions {

  /**
   * This is the game event handler associated.
   */
  private final GameEventHandler gameEventHandler;

  /**
   * This executor service is used to execute asynchronously the events received
   * from the server.
   */
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  /**
   * This constructor creates a new RMIGameView.
   * 
   * @param gameEventHandler the event handler for the game events.
   * @throws RemoteException thrown when a communication error occurs.
   */
  public RMIGameView(GameEventHandler gameEventHandler)
      throws RemoteException {
    this.gameEventHandler = gameEventHandler;
  }

  /**
   * {@inheritDoc}
   * The method is used by the server to send an event to the client.
   * Once received, the event is executed.
   */
  @Override
  public void transmitEvent(GameEvent event) throws RemoteException {
    executorService.submit(() -> event.execute(gameEventHandler));
  }

}
