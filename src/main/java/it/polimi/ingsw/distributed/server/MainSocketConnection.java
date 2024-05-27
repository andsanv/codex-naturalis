package it.polimi.ingsw.distributed.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.main.MainEvent;

public class MainSocketConnection implements MainViewActions, Runnable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public MainSocketConnection(Socket socket, ObjectInputStream in) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = in;
    }

    @Override
    public synchronized void receiveEvent(MainEvent serverEvent) throws RemoteException {
        try {
            out.writeObject(serverEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
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
}
