package it.polimi.ingsw.distributed.interfaces;

import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.game.GameCommand;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the game server.
 */
public interface GameServerActions extends Remote {
  /**
   * This method is used to transmit a game command to the server.
   * 
   * @param command the command to be sent
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  public void transmitCommand(GameCommand command) throws RemoteException, IOException;

  /**
   * This method must be called when connecting to the game server.
   *
   * @param userInfo       the user's information
   * @param clientMainView the client's game view
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  public void connectToGame(UserInfo userInfo, GameViewActions clientGameView) throws RemoteException, IOException;
}