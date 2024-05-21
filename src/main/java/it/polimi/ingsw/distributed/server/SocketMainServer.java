package it.polimi.ingsw.distributed.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.events.main.MainEvent;

public class SocketMainServer implements MainViewActions, Runnable {
    private ServerSocket serverSocket;
    private List<Socket> sockets;

    public SocketMainServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
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
        
    }

	@Override
	public void receiveEvent(MainEvent serverEvent) throws RemoteException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'receiveEvent'");
	}
}
