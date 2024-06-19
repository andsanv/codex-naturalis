package it.polimi.ingsw.distributed.client.rmi;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import java.rmi.RemoteException;

/**
 * This class is the RMI implementation of the GameViewActions interface.
 * So it used from the server to perform actions on the client game view.
 */
public class RMIGameView implements GameViewActions {

  /**
   * This is the game event handler
   */
  private final GameEventHandler gameEventHandler;

  public RMIGameView(GameEventHandler gameEventHandler) throws RemoteException {
    this.gameEventHandler = gameEventHandler;
  }

  /**
   * {@inheritDoc}
   * The method is used by the server to send an event to the client.
   * Once received, the event is executed.
   */
  @Override
  public void receiveEvent(GameEvent event) throws RemoteException {
    event.execute(gameEventHandler);
  }
}
