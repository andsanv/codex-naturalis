package it.polimi.ingsw.distributed.server.rmi;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.server.MainServerActions;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents the RMI Main server to connect to.
 * The main server is used in client communications that are not in-game.
 * The class implements runnable so it can be delegated to a dedicated thread.
 */
public class RMIMainServer extends UnicastRemoteObject implements MainServerActions, Runnable {

  /**
   * This executor service is used to make rmi function calls async.
   * The execution of the functions on the server.java instace are done in separate threads.
   */
  private final ExecutorService executorService;

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
    } catch (RemoteException e) {
      System.err.println("Error: " + e.toString());
      e.printStackTrace();
    } catch (AlreadyBoundException e) {
      System.err.println("Error: " + Config.RMIServerName + " already bound");
      e.printStackTrace();
    }
  }

  /**
   * This method is used from the client (via RMI) to connect to the main server.
   * The addConnectedClient method will verify the username and reply to the client with a UserInfoEvent giving him an adequate id.
   */
  @Override
  public void connectToMain(UserInfo userInfo, MainViewActions clientMainView)
      throws RemoteException {
        System.out.println("User " + userInfo.name + "main view " + clientMainView);
    executorService.submit(
        () -> {
          Server.INSTANCE.addConnectedClient(userInfo.name, clientMainView);
        });
  }

  /**
   * This method is used from the client when it wants to reconnect to the server.
   * The addReconnectedClient will verify the username and reply to the client with a UserInfoEvent giving him an adequate id.
   */
  @Override
  public void reconnect(UserInfo userInfo, MainViewActions clientMainView) throws RemoteException {
    executorService.submit(
        () -> {
          Server.INSTANCE.addReconnectedClient(userInfo, clientMainView);
        });
  }

  /**
   * This method is used from the client to send a command to the server.
   * Commands received are executed calling the execute method.
   */
  @Override
  public void send(MainCommand command) throws RemoteException {
    System.out.println(command);
    executorService.submit(command::execute);
  }
}