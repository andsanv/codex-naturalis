package it.polimi.ingsw.distributed.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.events.main.MainEvent;

// Questa classe può essere eliminata perché sostituita dal connectionhandler
public class SocketMainView implements MainViewActions, Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketMainView(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void receiveEvent(MainEvent serverEvent) throws RemoteException {

    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = in.readLine()) != null) {
                switch (input) {
                    case "sendError":
                        
                        break;
                
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
