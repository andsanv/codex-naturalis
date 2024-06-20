package it.polimi.ingsw.distributed.server;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the main server.
 */
public interface MainServerActions extends Remote {

  /**
   * This method is used to transmit a main command to the server.
   * @param command the command to be sent
   * @throws RemoteException
   */
  public void send(MainCommand command) throws RemoteException;

  /**
   * This method is used when connecting to the server with an already existing account. The client
   * can retrieve the UserInfo, for example, from a config file he created after connecting for the
   * first time.
   *
   * @param userInfo the user information
   * @param clientMainView the client's main view
   * @param gameViewActions the client's game view
   * @throws RemoteException
   */
  void reconnect(UserInfo userInfo, MainViewActions clientMainView, GameViewActions gameViewActions) throws RemoteException;

  /**
   * This method must be called when connecting to the server for the first time.
   *
   * @param username the client's username
   * @param clientMainView the client's main view
   * @param gameViewActions the client's game view
   * @throws RemoteException
   */
  void connectToMain(String username, MainViewActions clientMainView, GameViewActions gameViewActions) throws RemoteException;
}