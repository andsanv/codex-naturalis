package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.KeepAliveCommand;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.distributed.events.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketConnectionHandler extends ConnectionHandler {
  private Socket socket;

  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;

  public SocketConnectionHandler(UI userInterface) throws UnknownHostException, IOException {
    super(userInterface);

    socket = new Socket(Config.ServerIP, Config.MainSocketPort);

    this.outputStream = new ObjectOutputStream(socket.getOutputStream());
    this.outputStream.flush();
    this.inputStream = new ObjectInputStream(socket.getInputStream());

    System.out.println("Connected to server and set up streams");
    new Thread(
            () -> {
              while (true) {
                try {
                  Event event = (Event) inputStream.readObject();
                  System.out.println(event);
                  
                  if(event instanceof KeepAliveEvent) {
                    System.out.println("Received keep alive event");
                  } else if (event instanceof GameEvent) {
                    GameEvent gameEvent = (GameEvent) event;
                    gameEvent.execute(userInterface);
                  } else if (event instanceof MainEvent) {
                    MainEvent mainEvent = (MainEvent) event;
                    mainEvent.execute(userInterface);
                  } else {
                    System.err.println("Received unknown event");
                  }

                } catch (IOException e) {
                  System.err.println("Failed to receive event");
                  System.err.println("Server probably disconnected");

                  break;
                } catch (ClassNotFoundException e) {
                  System.err.println("Failed to decode event");
                  e.printStackTrace();
                }
              }
            })
        .start();

    System.out.println("started listening for events");
  }

  @Override
  public boolean sendToMainServer(MainCommand command) {
    try {
      outputStream.writeObject(command);
      System.out.println("Sent command to main server");
      outputStream.reset();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @Override
  public boolean sendToGameServer(GameCommand command) {
    try {
      outputStream.writeObject(command);
      outputStream.reset();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @Override
  public boolean reconnect() {
    ReconnectionCommand reconnectionCommand = new ReconnectionCommand(userInterface.getUserInfo());
    
    try {
      socket = new Socket(Config.ServerIP, Config.MainSocketPort);

      this.outputStream = new ObjectOutputStream(socket.getOutputStream());
      this.outputStream.flush();

      this.inputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Failed to reconnect to server");
      return false;
    }

    System.out.println("Reconnected to server");

    return sendToMainServer(reconnectionCommand);
  }

  public void close() {
    try {
      outputStream.close();
      inputStream.close();
      socket.close();
    } catch (IOException e) {
      System.err.println("Failed to close socket");
    }
    System.out.println("Closed socket");
  }
}
