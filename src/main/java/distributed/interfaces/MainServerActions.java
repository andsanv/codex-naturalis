package distributed.interfaces;

import controller.usermanagement.UserInfo;
import distributed.commands.main.MainCommand;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the action that can be performed on the main server.
 */
public interface MainServerActions extends Remote {

  /**
   * This method is used to transmit a main command to the server.
   * 
   * @param command the command to be sent
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  public void transmitCommand(MainCommand command) throws RemoteException, IOException;

  /**
   * This method is used when connecting to the server with an already existing
   * account. The client
   * can retrieve the UserInfo, for example, from a config file he created after
   * connecting for the
   * first time.
   *
   * @param userInfo        the user information
   * @param clientMainView  the client's main view
   * @param gameViewActions the client's game view
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  void reconnect(UserInfo userInfo, MainViewActions clientMainView, GameViewActions gameViewActions)
      throws RemoteException, IOException;

  /**
   * This method must be called when connecting to the server for the first time.
   *
   * @param username        the client's username
   * @param clientMainView  the client's main view
   * @param gameViewActions the client's game view
   * @throws RemoteException thrown when a communication error occurs
   * @throws IOException     thrown when an I/O error occurs
   */
  void connectToMain(String username, MainViewActions clientMainView, GameViewActions gameViewActions)
      throws RemoteException, IOException;

    boolean ping() throws RemoteException;
}