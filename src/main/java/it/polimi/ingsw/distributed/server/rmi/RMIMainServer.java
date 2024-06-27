package it.polimi.ingsw.distributed.server.rmi;

import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.ServerPrinter;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.interfaces.GameViewActions;
import it.polimi.ingsw.distributed.interfaces.MainServerActions;
import it.polimi.ingsw.distributed.interfaces.MainViewActions;

/**
 * This class represents the RMI Main server to connect to.
 * The main server is used in client communications that are not in-game.
 * The class implements runnable so it can be delegated to a dedicated thread.
 */
public class RMIMainServer extends UnicastRemoteObject implements MainServerActions, Runnable {

  /**
   * This executor service is used to make rmi function calls async.
   * The execution of the functions on the server.java instace are done in
   * separate threads.
   */
  private final ExecutorService executorService;

  /**
   * This constructor creates a new RMIMainServer.
   * 
   * @throws RemoteException thrown when a communication error occurs.
   */
  public RMIMainServer() throws RemoteException {
    executorService = Executors.newCachedThreadPool();
  }

  /**
   * This method sets up the RMI server registry.
   */
  @Override
  public void run() {
    Registry registry;

    try {   
      registry = LocateRegistry.createRegistry(Config.RMIServerPort);
      registry.bind(Config.RMIServerName, this);

      ServerPrinter.displayInfo("RMIServer started on port " + Config.RMIServerPort);
    } catch (RemoteException e) {
      ServerPrinter.displayError("Error: " + e.toString());
      e.printStackTrace();
    } catch (AlreadyBoundException e) {
      ServerPrinter.displayError("Error: " + Config.RMIServerName + " already bound");
      e.printStackTrace();
    } catch (Exception e) {
      ServerPrinter.displayError("Error: " + e.toString());
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * This method is used from the client (via RMI) to connect to the main server.
   * The addConnectedClient method will verify the username and reply to the
   * client with a UserInfoEvent giving him an adequate id.
   */
  @Override
  public void connectToMain(String username, MainViewActions clientMainView, GameViewActions gameViewActions)
      throws RemoteException {

    RMIHandler rmiHandler = new RMIHandler(clientMainView, gameViewActions);

    ServerPrinter.displayDebug("Received connection request from username: " + username);;
    executorService.submit(
        () -> {
          Server.INSTANCE.clientSignUp(username, rmiHandler);
        });
  }

  /**
   * {@inheritDoc}
   * This method is used from the client when it wants to reconnect to the server.
   * The addReconnectedClient will verify the username and reply to the client
   * with a UserInfoEvent giving him an adequate id.
   */
  @Override
  public void reconnect(UserInfo userInfo, MainViewActions clientMainView, GameViewActions gameViewActions)
      throws RemoteException {

    RMIHandler rmiHandler = new RMIHandler(clientMainView, gameViewActions);

    ServerPrinter.displayDebug("Received reconnection request from username: " + userInfo);

    executorService.submit(
        () -> {
          Server.INSTANCE.clientLogin(userInfo, rmiHandler);
        });
  }

  /**
   * {@inheritDoc}
   * This method is used from the client to send a command to the server.
   * Commands received are executed calling the execute method.
   */
  @Override
  public void transmitCommand(MainCommand command) throws RemoteException {
    executorService.submit(command::execute);
  }
}