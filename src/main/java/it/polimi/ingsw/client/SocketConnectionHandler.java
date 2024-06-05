package it.polimi.ingsw.client;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
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
    this.inputStream = new ObjectInputStream(socket.getInputStream());

    new Thread(
            () -> {
              while (true) {
                try {
                  Event event = (Event) inputStream.readObject();

                  if (event instanceof GameEvent) {
                    GameEvent gameEvent = (GameEvent) event;
                    gameEvent.execute(userInterface);
                  } else if (event instanceof MainEvent) {
                    MainEvent mainEvent = (MainEvent) event;
                    mainEvent.execute(userInterface);
                  }

                } catch (IOException e) {
                  System.err.println("Failed to receive event");
                  e.printStackTrace();
                } catch (ClassNotFoundException e) {
                  System.err.println("Failed to decode event");
                  e.printStackTrace();
                }
              }
            })
        .start();
  }

  @Override
  public boolean sendToMainServer(MainCommand command) {
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
    // TODO
    return false;
  }
}
