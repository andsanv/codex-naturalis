package it.polimi.ingsw.distributed.server;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the game server.
 */
public interface GameServerActions extends Remote {
  /**
   * This method is used to transmit a game command to the server.
   * @param command the command to be sent
   * @throws RemoteException
   */
  public void send(GameCommand command) throws RemoteException;

  /**
   * This method must be called when connecting to the game server.
   *
   * @param userInfo the user's information
   * @param clientMainView the client's game view
   * @throws RemoteException
   */
  public void connectToGame(UserInfo userInfo, GameViewActions clientGameView)
      throws RemoteException;
}