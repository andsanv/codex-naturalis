package it.polimi.ingsw.distributed.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;

/**
 * Accepts incoming socket connection to the main server.
 * It's required to use only one instance of this class.
 */
public class SocketMainServer {
    private ServerSocket serverSocket;

    private final ExecutorService executorService;

    private final ConcurrentHashMap<UserInfo, MainSocketConnection> connections;

    public SocketMainServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newCachedThreadPool();
        this.connections = new ConcurrentHashMap<>();

        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    String requestType = (String) in.readObject();

                    MainSocketConnection connection = new MainSocketConnection(socket, in);
                    
                    if ("connection".equals(requestType)) {
                        String username = (String) in.readObject();

                        executorService.submit(connection);
                        UserInfo userInfo = Server.INSTANCE.addConnectedClient(username, connection);
                        connections.put(userInfo, connection);
                    } else if ("reconnection".equals(requestType)) {
                        UserInfo userInfo = (UserInfo) in.readObject();

                        executorService.submit(connection);
                        Server.INSTANCE.addReconnectedClient(userInfo, connection);
                        connections.put(userInfo, connection);
                    } else {
                        System.err.println("Unrecognized request on main socket server: " + requestType);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
