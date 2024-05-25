package it.polimi.ingsw.distributed.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.events.main.MainErrorEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;

public class SocketMainServer implements MainViewActions, Runnable {
    private ServerSocket serverSocket;
    private final Set<Socket> sockets;

    private final ExecutorService executorService;

    public SocketMainServer(int port) throws IOException {
        this.sockets = Collections.synchronizedSet(new HashSet<>());
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newCachedThreadPool();

        new Thread(() -> {
            while (true) {
                try {
                    sockets.add(serverSocket.accept());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {

            }
        }).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sockets.add(serverSocket.accept());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        receiveEvent(new MainErrorEvent(error));
    }

	@Override
	public void receiveEvent(MainEvent serverEvent) throws RemoteException {
        synchronized (sockets) {
            for (Socket socket : sockets) {
                try {
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(serverEvent);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
	}
}
