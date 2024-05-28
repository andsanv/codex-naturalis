package it.polimi.ingsw.distributed.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.GameEvent;

public class GameSocketConnection implements GameViewActions, Runnable {
    private final ObjectOutputStream out;
    private final GameFlowManager gameFlowManager;
    private final ObjectInputStream in;

    public GameSocketConnection(ObjectInputStream in, ObjectOutputStream out, GameFlowManager gameFlowManager) throws IOException {
        this.out = out;
        this.gameFlowManager = gameFlowManager;
        this.in = in;
    }

    public GameSocketConnection(Socket socket, ObjectInputStream in, GameFlowManager gameFlowManager) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.gameFlowManager = gameFlowManager;
        this.in = in;
    }

//    public GameSocketConnection(ObjectOutputStream out, ObjectInputStream in, GameFlowManager gameFlowManager) throws IOException {
//        this.out = out;
//        this.gameFlowManager = gameFlowManager;
//        this.in = in;
//    }


    @Override
    public synchronized void receiveEvent(GameEvent gameEvent) throws RemoteException {
        try {
            out.writeObject(gameEvent);
            out.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                GameCommand command = (GameCommand) in.readObject();
                command.execute(gameFlowManager);
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
}
