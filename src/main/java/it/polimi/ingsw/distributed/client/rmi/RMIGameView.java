package it.polimi.ingsw.distributed.client.rmi;

import it.polimi.ingsw.distributed.client.RMIConnectionHandler;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.interfaces.GameViewActions;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
   * This conncetion handler will be used to handle the communication with the
   * server.
   */
  private final RMIConnectionHandler connectionHandler;

  /**
   * This constructor creates a new RMIGameView.
   * 
   * @param gameEventHandler the event handler for the game events.
   * @throws RemoteException thrown when a communication error occurs.
   */
  public RMIGameView(GameEventHandler gameEventHandler, RMIConnectionHandler rmiConnectionHandler) throws RemoteException {
    this.gameEventHandler = gameEventHandler;
    this.connectionHandler = rmiConnectionHandler;
  }

  /**
   * {@inheritDoc}
   * The method is used by the server to send an event to the client.
   * Once received, the event is executed.
   */
  @Override
  public void transmitEvent(GameEvent event) throws RemoteException {
    event.execute(gameEventHandler);
  }

}
