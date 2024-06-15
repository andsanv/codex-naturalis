package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.client.rmi.RMIMainView;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.distributed.server.GameServerActions;
import it.polimi.ingsw.distributed.server.MainServerActions;
import it.polimi.ingsw.view.UI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RMIConnectionHandler extends ConnectionHandler {

  private MainServerActions mainServerActions;
  private GameServerActions gameServerActions;

  private final BlockingQueue<MainCommand> serverCommandQueue;
  private final BlockingQueue<GameCommand> gameCommandQueue;

  private RMIMainView clientMainView;

  public RMIConnectionHandler(UI userInterface) throws Exception {
    super(userInterface);

    serverCommandQueue = new LinkedBlockingQueue<>();
    gameCommandQueue = new LinkedBlockingQueue<>();

    gameServerActions = null;
    

    try {
      Registry registry = LocateRegistry.getRegistry(Config.RMIServerPort);
      mainServerActions = (MainServerActions) registry.lookup(Config.RMIServerName);
      this.clientMainView = new RMIMainView(userInterface);

      new Thread(new CommandConsumer<>(serverCommandQueue, this)).start();
      new Thread(new CommandConsumer<>(gameCommandQueue, this)).start();
    } catch (Exception e) {
      throw new Exception("Failed to connect to RMI server");
    }
  }

  // Methods to insert commands to the queue, handled by the CommandConsumer
  public void addCommand(MainCommand command) {
    serverCommandQueue.add(command);
  }

  public void addCommand(GameCommand command) {
    gameCommandQueue.add(command);
  }

  @Override
  public boolean sendToMainServer(MainCommand mainCommand) {
    if(mainCommand instanceof ConnectionCommand) {
      try {
        mainServerActions.connectToMain(((ConnectionCommand) mainCommand).username, this.clientMainView);
      
        System.out.println("Waiting for user info");
        System.out.println(userInterface.getUserInfo());
        while(userInterface.getUserInfo() == null) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
        System.out.println(userInterface.getUserInfo());
        
        return true;
      } catch (RemoteException e) {
        e.printStackTrace();
        return false;
      }
    } else if (mainCommand instanceof ReconnectionCommand) {
      try {
        this.clientMainView = new RMIMainView(userInterface);
        
        System.out.println(this.clientMainView);
        mainServerActions.reconnect(((ReconnectionCommand) mainCommand).userInfo, this.clientMainView);
      } catch (RemoteException e) {
        e.printStackTrace();
        return false;
      }
    } else {
      try {
        mainServerActions.send(mainCommand);
        return true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return false;
  }

  @Override
  public boolean sendToGameServer(GameCommand gameCommand) {
    try {
      if (gameServerActions != null) {
        gameServerActions.send(gameCommand);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public boolean reconnect() {
    ReconnectionCommand reconnectionCommand = new ReconnectionCommand(userInterface.getUserInfo());
    System.out.println(3);
    return sendToMainServer(reconnectionCommand);
  }

  public void setGameServerActions(GameServerActions gameServerActions) {
    this.gameServerActions = gameServerActions;
  }

  public BlockingQueue<MainCommand> getServerCommandQueue() {
    return serverCommandQueue;
  }

  public BlockingQueue<GameCommand> getGameCommandQueue() {
    return gameCommandQueue;
  }

}
