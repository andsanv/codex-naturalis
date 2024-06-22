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

/**
 * This class represents the RMI Game server to connect to.
 * The game server is used in in-game communications.
 * The class implements runnable so that when the server controller creates the
 * RMIGameServer can delegate its execution to a new thread.
 */
public class RMIGameServer extends UnicastRemoteObject implements Runnable, GameServerActions {

  /**
   * This is a reference to the gameFlowManager the client is playing the game
   * with.
   * It needs to execute the commands received from the client.
   */
  private final GameFlowManager gameFlowManager;

  /**
   * This string contains the information for the RMI connection.
   */
  private final String rmiConnectionInfo;

  /**
   * This map the GameViewAction for each client.
   */
  private ConcurrentHashMap<UserInfo, GameViewActions> clients;

  /**
   * This executor service is used to make rmi function calls async.
   * The execution of the functions on the server.java instace are done in
   * separate threads.
   */
  private final ExecutorService executorService;

  /**
   * The constructor initializes the attributes and assign the parameters.
   * 
   * @param gameFlowManager   the gameflowmanager of the started game
   * @param rmiConnectionInfo the rmi connection info
   * @throws RemoteException
   */
  public RMIGameServer(GameFlowManager gameFlowManager, String rmiConnectionInfo)
      throws RemoteException {
    executorService = Executors.newCachedThreadPool();
    this.gameFlowManager = gameFlowManager;
    this.rmiConnectionInfo = rmiConnectionInfo;
  }

  /**
   * {@inheritDoc}
   * This method is used from the client to send a command to the server.
   * The command is added to the command queue in the gameFlowManager.
   */
  @Override
  public void transmitCommand(GameCommand command) throws RemoteException {
    executorService.submit(() -> gameFlowManager.addCommand(command));
  }

  /**
   * {@inheritDoc}
   * This method is used from the client to connect to the game server.
   * It is simply added to the clients map along with its GameViewActions.
   */
  @Override
  public void connectToGame(UserInfo userInfo, GameViewActions clientGameView)
      throws RemoteException {
    clients.put(userInfo, clientGameView);
  }

  /**
   * This method sets up the RMI server registry.
   */
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