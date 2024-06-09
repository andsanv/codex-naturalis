package it.polimi.ingsw.distributed.server.rmi;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.server.GameServerActions;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RMIGameServer extends UnicastRemoteObject implements Runnable, GameServerActions {

  private final GameFlowManager gameFlowManager;

  private final String rmiConnectionInfo;

  private ConcurrentHashMap<UserInfo, GameViewActions> clients;

  private final ExecutorService executorService;

  public RMIGameServer(GameFlowManager gameFlowManager, String rmiConnectionInfo)
      throws RemoteException {
    executorService = Executors.newCachedThreadPool();
    this.gameFlowManager = gameFlowManager;
    this.rmiConnectionInfo = rmiConnectionInfo;
  }

  @Override
  public void send(GameCommand command) throws RemoteException {
    executorService.submit(() -> gameFlowManager.addCommand(command));
  }

  @Override
  public void connectToGame(UserInfo userInfo, GameViewActions clientGameView)
      throws RemoteException {
    clients.put(userInfo, clientGameView);
  }

  @Override
  public void run() {
    Registry registry;

    try {
      registry = LocateRegistry.getRegistry(Config.RMIServerPort);
      registry.bind(rmiConnectionInfo, this);
    } catch (RemoteException e) {
      System.err.println("Error: " + e.toString());
      e.printStackTrace();
    } catch (AlreadyBoundException e) {
      System.err.println("Error: " + rmiConnectionInfo + " already bound");
      e.printStackTrace();
    }
  }
}
