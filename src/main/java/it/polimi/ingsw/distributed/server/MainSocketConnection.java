package it.polimi.ingsw.distributed.server;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.main.MainEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainSocketConnection implements MainViewActions, Runnable {
  private final ObjectOutputStream out;
  private final ObjectInputStream in;
  AtomicBoolean startedGame = new AtomicBoolean(false);

  public MainSocketConnection(Socket socket, ObjectInputStream in) throws IOException {
    this.out = new ObjectOutputStream(socket.getOutputStream());
    this.in = in;
  }

  @Override
  public synchronized void receiveEvent(MainEvent serverEvent) throws RemoteException {
    try {
      out.writeObject(serverEvent);
      out.reset();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (!startedGame.get()) {
      try {
        // TODO check deadlock
        MainCommand command = (MainCommand) in.readObject();

        command.execute();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassCastException e) {
        e.printStackTrace();
      }
    }
  }

  public GameSocketConnection switchConnection(GameFlowManager gameflowManager) throws IOException {
    startedGame.set(true);
    return new GameSocketConnection(in, out, gameflowManager);
  }
}
