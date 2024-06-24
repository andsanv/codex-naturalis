package it.polimi.ingsw.view.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.commands.main.ReconnectionCommand;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.main.KeepAliveEvent;
import it.polimi.ingsw.distributed.events.main.LobbiesEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import it.polimi.ingsw.distributed.events.main.ReconnectToGameEvent;
import it.polimi.ingsw.distributed.events.main.RefusedConnectionEvent;
import it.polimi.ingsw.view.UI;

/**
 * This class is the socket implementation of the ConnectionHandler interface.
 */
public class SocketConnectionHandler extends ConnectionHandler {
  /**
   * This is the socket instance used for the communication.
   */
  private Socket socket;

  /**
   * These are the input and output streams used to communicate.
   */
  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;

  /**
   * this constructor creates a new SocketConnectionHandler.
   * It tries to connect to the server and set the streams.
   * Then calls the createListenerThread function.
   * 
   * @param userInterface the user interface of the client
   * @throws UnknownHostException
   * @throws IOException
   */
  public SocketConnectionHandler(UI userInterface) throws UnknownHostException, IOException {
    super(userInterface);

    socket = new Socket(Config.ServerIP, Config.MainSocketPort);

    this.outputStream = new ObjectOutputStream(socket.getOutputStream());
    this.outputStream.flush();
    this.inputStream = new ObjectInputStream(socket.getInputStream());

    // System.out.println("Connected to server and set up streams");

    createListenerThread();

    // System.out.println("Started listening for events");
  }

  /**
   * This method keeps listening for events from the server.
   * Based on the type of the event, it executes the event, except for
   * KeepAliveEvent.
   * If a problem occurs, the method quits the loop.
   */
  private void createListenerThread() {
    new Thread(
        () -> {
          while (true) {
            try {
              Event event = (Event) inputStream.readObject();
              // System.out.println("Received event: " + event);

              if (event instanceof KeepAliveEvent) {
                ;
              } else if (event instanceof GameEvent) {
                GameEvent gameEvent = (GameEvent) event;
                gameEvent.execute(userInterface);
              } else if (event instanceof MainEvent) {
                MainEvent mainEvent = (MainEvent) event;
                mainEvent.execute(userInterface);
              } else {
                // System.err.println("Received unknown event");
              }

            } catch (IOException e) {
              this.isConnected.set(false);
              break;
            } catch (ClassNotFoundException e) {
              this.isConnected.set(false);
              break;
            }
          }
        })
        .start();
  }

  /**
   * This method sends a main command to the main server.
   * The command can be sent only if the client has a UserInfo assigned to
   * it.polimi.ingsw.view.connection
   * 
   * @param command the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  @Override
  public boolean sendToMainServer(MainCommand command) {
    // System.out.println("Sending command to main server");
    if (userInterface.getUserInfo() == null && !(command instanceof ReconnectionCommand)
        && !(command instanceof ConnectionCommand)) {
      this.isConnected.set(false);
      return false;
    }
    try {
      outputStream.writeObject(command);
      outputStream.reset();
    } catch (IOException e) {
      this.isConnected.set(false);
      return false;
    }

    this.isConnected.set(true);
    return true;
  }

  /**
   * This method sends a game command to the server.
   * The command can be sent only if the client has a UserInfo assigned to it
   * 
   * @param command the command to be sent
   * @return true if the command was sent successfully, false otherwise
   */
  @Override
  public boolean sendToGameServer(GameCommand command) {
    if (userInterface.getUserInfo() == null) {
      this.isConnected.set(false);
      return false;
    }
    try {
      outputStream.writeObject(command);
      outputStream.reset();
    } catch (IOException e) {
      this.isConnected.set(false);
      return false;
    }

    this.isConnected.set(true);
    return true;
  }

  /**
   * This method is called when the client wants to try to reconnect to the
   * server.
   * It sends a ReconnectionCommand to the server and waits for the response.
   * While waiting, it keeps listening for events and puts them in a queue.
   * Once received a response event (ReconnectToGameEvent, LobbiesEvent,
   * RefusedConnectionEvent), it executes it.
   * After that, it executes all the events in the queue and finally calls the
   * createListenerThread function.
   * 
   * @return true if the reconnection was successful, false otherwise
   */
  @Override
  public boolean reconnect() {
    this.close();

    ReconnectionCommand reconnectionCommand = new ReconnectionCommand(userInterface.getUserInfo());

    try {
      socket = new Socket(Config.ServerIP, Config.MainSocketPort);

      this.outputStream = new ObjectOutputStream(socket.getOutputStream());
      this.outputStream.flush();

      this.inputStream = new ObjectInputStream(socket.getInputStream());

      sendToMainServer(reconnectionCommand);

      new Thread(() -> {
        Queue<Event> events = new ConcurrentLinkedQueue<>();
        while (true) {
          // System.out.println("Waiting for events");
          try {
            Object obj = inputStream.readObject();
            // System.out.println("Received object: " + obj);

            Event event = (Event) obj;
            // System.out.println("Received event (reconnect): " + event);

            if (event instanceof ReconnectToGameEvent) {
              ReconnectToGameEvent reconnectToGameEvent = (ReconnectToGameEvent) event;
              reconnectToGameEvent.execute(userInterface);
              break;
            } else if (event instanceof LobbiesEvent) {
              LobbiesEvent lobbiesEvent = (LobbiesEvent) event;
              lobbiesEvent.execute(userInterface);
              break;
            } else {
              events.add(event);
            }

          } catch (IOException e) {
            // System.err.println("Failed to receive event");
            // System.err.println("Server probably disconnected");
            e.printStackTrace();
            
            break;
          } catch (ClassNotFoundException e) {
            // System.err.println("Failed to decode event");
            e.printStackTrace();
          }
        }

        while (!events.isEmpty()) {
          Event event = events.poll();
          // System.out.println("Queued event: " + event);
          if (event instanceof KeepAliveEvent) {
            ;
          } else if (event instanceof GameEvent) {
            GameEvent gameEvent = (GameEvent) event;
            gameEvent.execute(userInterface);
          } else if (event instanceof MainEvent) {
            MainEvent mainEvent = (MainEvent) event;
            mainEvent.execute(userInterface);
          } else {
            // System.err.println("Received unknown event");
          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        createListenerThread();

      }).start();

    } catch (IOException e) {
      e.printStackTrace();
      // System.err.println("Failed to reconnect to server");
      this.isConnected.set(false);
      return false;
    }

    return true;
  }

  /**
   * This method is used to close the socket and the streams.
   * 
   * @return true if the socket was closed successfully, false otherwise
   */
  public boolean close() {
    try {
      outputStream.close();
      inputStream.close();
      socket.close();
    } catch (IOException e) {
      // System.err.println("Failed to close socket");
      return false;
    }

    // System.out.println("Closed socket");
    return true;
  }
}
