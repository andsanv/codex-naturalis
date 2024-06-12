package it.polimi.ingsw.view.connection;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.commands.Command;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.distributed.events.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.RefusedConnectionEvent;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    //System.out.println("Connected to server and set up streams");
    
    createListenerThread();

    //System.out.println("started listening for events");
  }

  private void createListenerThread() {
    new Thread(
            () -> {
              while (true) {
                try {
                  Event event = (Event) inputStream.readObject();
                  //System.out.println("Received event: " + event);
                  
                  if(event instanceof KeepAliveEvent) {
                    ;
                  } else if (event instanceof GameEvent) {
                    GameEvent gameEvent = (GameEvent) event;
                    gameEvent.execute(userInterface);
                  } else if (event instanceof MainEvent) {
                    MainEvent mainEvent = (MainEvent) event;
                    mainEvent.execute(userInterface);
                  } else {
                    //System.err.println("Received unknown event");
                  }

                } catch (IOException e) {
                  //System.err.println("Failed to receive event");
                  //System.err.println("Server probably disconnected");

                  break;
                } catch (ClassNotFoundException e) {
                  //System.err.println("Failed to decode event");
                  e.printStackTrace();
                }
              }
            })
        .start();
  }

  @Override
  public boolean sendToMainServer(MainCommand command) {
    if(userInterface.getUserInfo() == null) {
      //System.err.println("User info is null");
      return false;
    }
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
    if(userInterface.getUserInfo() == null) {
      //System.err.println("User info is null");
      return false;
    }
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

      sendToMainServer(reconnectionCommand);

      new Thread(()-> {
        Queue<Event> events = new ConcurrentLinkedQueue<>();
        while(true) {
          System.out.println("Waiting for events");
          try {
            Object obj = inputStream.readObject();
            System.out.println("Received object: " + obj);

            Event event = (Event) obj;
            System.out.println("Received event (reconnect): " + event);

            if(event instanceof ReconnectToGameEvent) {
              ReconnectToGameEvent reconnectToGameEvent = (ReconnectToGameEvent) event;
              reconnectToGameEvent.execute(userInterface);
              break;
            } else if(event instanceof LobbiesEvent) {
              LobbiesEvent lobbiesEvent = (LobbiesEvent) event;
              lobbiesEvent.execute(userInterface);
              break;
            } else if(event instanceof RefusedConnectionEvent) {
              RefusedConnectionEvent refusedConnectionEvent = (RefusedConnectionEvent) event;
              refusedConnectionEvent.execute(userInterface);
              break;
            } else {
              events.add(event);
            }
            
          } catch (IOException e) {
            System.err.println("Failed to receive event");
            System.err.println("Server probably disconnected");
            e.printStackTrace();

            break;
          } catch (ClassNotFoundException e) {
            System.err.println("Failed to decode event");
            e.printStackTrace();
          }
        }

        while(!events.isEmpty()) {
          Event event = events.poll();
          if(event instanceof KeepAliveEvent) {
            ;
          } else if (event instanceof GameEvent) {
            GameEvent gameEvent = (GameEvent) event;
            gameEvent.execute(userInterface);
          } else if (event instanceof MainEvent) {
            MainEvent mainEvent = (MainEvent) event;
            mainEvent.execute(userInterface);
          } else {
            System.err.println("Received unknown event");
          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }).start();      

      createListenerThread();

      
    } catch (IOException e) {
      e.printStackTrace();
      //System.err.println("Failed to reconnect to server");
      return false;
    }

    return true;
  }

  public boolean close() {
    try {
      outputStream.close();
      inputStream.close();
      socket.close();
    } catch (IOException e) {
      //System.err.println("Failed to close socket");
      return false;
    }

    //System.out.println("Closed socket");
    return true;
  }
}
