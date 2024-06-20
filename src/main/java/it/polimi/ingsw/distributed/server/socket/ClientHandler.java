package it.polimi.ingsw.distributed.server.socket;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.observer.Observable;
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

/**
 * This class will handle the communication with the client.
 * Receiving command from the client and  sending events to it.
 */
public class ClientHandler implements Runnable, MainViewActions, GameViewActions {

  /**
   * The input stream for the client Command requests
   */
  private final ObjectInputStream in;

  /**
   * The output stream for the Event server responses
   */
  private final ObjectOutputStream out;

  /**
   * This is a reference to the gameFlowManager the clientHandler is connected to
   */
  private GameFlowManager gameFlowManager;

  /**
   * The constructor initializes the streams
   * @param out the output stream
   * @param in the input stream
   */
  public ClientHandler(ObjectOutputStream out, ObjectInputStream in) {
    this.out = out;
    this.in = in;
  }

  /**
   * This method keeps waiting for reading commands from the client.
   * If the received command is a GameCommand, after checking if the gameFlowManager is set, it is executed.
   * If the received command is a MainCommand, it is executed.
   * If exceptions are thrown, the loop is stopped and a new ClientHandler should be created (sending a new connection request to the socket server).
   */
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
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void receiveEvent(MainEvent event) throws IOException {
    out.writeObject(event);
    out.reset();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void receiveEvent(GameEvent event) throws IOException {
    out.writeObject(event);
    out.reset();
  }

  /**
   * This method sets the gameFlowManager for the clientHandler that is created only when a lobby starts a game.
   * @param gameFlowManager
   */
  public void setGameFlowManager(GameFlowManager gameFlowManager) {
    this.gameFlowManager = gameFlowManager;
  }

  /**
   * {@inheritDoc}
   * This method update the client on changes on the game model on the server.
   * This method is inherited from the Observer interface.
   */
  @Override
  public void update(GameEvent event) {
    try {
      receiveEvent(event);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * This method is not used in the socket implementation.
   */
  @Override
  public void setGameServer(GameServerActions gameServer) throws RemoteException {
    ;
  }
}