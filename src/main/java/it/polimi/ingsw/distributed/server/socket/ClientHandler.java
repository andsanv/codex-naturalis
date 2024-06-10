package it.polimi.ingsw.distributed.server.socket;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.server.GameServerActions;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

public class ClientHandler implements Runnable, MainViewActions, GameViewActions {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private GameFlowManager gameFlowManager;

  public ClientHandler(ObjectOutputStream out, ObjectInputStream in) throws IOException {
    this.out = out;
    this.in = in;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Command command = (Command) in.readObject();
        System.out.println("Received command: " + command);

        if (command instanceof GameCommand) {
          if (gameFlowManager == null) {
            System.err.println("GameFlowManager not set");
          }
          ((GameCommand) command).execute(gameFlowManager);
        } else if (command instanceof MainCommand) {
          ((MainCommand) command).execute();
        } else {
          System.err.println("Unrecognized command: " + command);
        }
      } catch (EOFException e) {
        System.out.println("Client disconnected");
        break;
      } catch (IOException | ClassNotFoundException e) {
        System.err.println("Error while reading command: " + e.getMessage());
        break;
      } finally {
        // Add your code here
      }
    }
  }

  @Override
  public void receiveEvent(MainEvent event) throws IOException {
    out.writeObject(event);
    out.reset();
    
  }

  @Override
  public void receiveEvent(GameEvent event) throws IOException {
    out.writeObject(event);
    out.reset();
    
  }

  public void setGameFlowManager(GameFlowManager gameFlowManager) {
    this.gameFlowManager = gameFlowManager;
  }

  @Override
  public void update(GameEvent event) {
    try {
      receiveEvent(event);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void setGameServer(GameServerActions gameServer) throws RemoteException {
    ;
  }
}
