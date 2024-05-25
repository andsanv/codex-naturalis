package it.polimi.ingsw.distributed.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import it.polimi.ingsw.controller.server.Server;

public class SocketMainServer {
    private ServerSocket serverSocket;

    public SocketMainServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String username = in.readLine();

                    Server.INSTANCE.addConnectedClient(username, new MainSocketConnection(socket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // new Thread(() -> {
        //     while (true) {
                
        //     }
        // }).start();
    }
}
