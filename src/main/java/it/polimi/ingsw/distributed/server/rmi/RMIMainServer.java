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

public class RMIMainServer extends UnicastRemoteObject implements MainServerActions, Runnable {

  private final ExecutorService executorService;

  public RMIMainServer() throws RemoteException {
    executorService = Executors.newCachedThreadPool();
  }

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

  @Override
  public void connectToMain(String username, MainViewActions clientMainView)
      throws RemoteException {
        System.out.println("User " + username + "main view " + clientMainView);
    executorService.submit(
        () -> {
          Server.INSTANCE.addConnectedClient(username, clientMainView);
        });
  }

  @Override
  public void reconnect(UserInfo userInfo, MainViewActions clientMainView) throws RemoteException {
    executorService.submit(
        () -> {
          Server.INSTANCE.addReconnectedClient(userInfo, clientMainView);
        });
  }

  @Override
  public void send(MainCommand command) throws RemoteException {
    System.out.println(command);
    executorService.submit(command::execute);
  }
}
