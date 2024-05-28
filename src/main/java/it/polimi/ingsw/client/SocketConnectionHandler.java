package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.distributed.events.main.GameConnectionEvent;
import it.polimi.ingsw.distributed.events.main.MainEvent;

public class SocketConnectionHandler extends ConnectionHandler {
    private Socket mainSocket;
    private Socket gameSocket;

    private ObjectOutputStream outputMainStream;
    private ObjectOutputStream outputGameStream;

    private ObjectInputStream inputMainStream;
    private ObjectInputStream inputGameStream;

    boolean connectedToGame = false;

    public SocketConnectionHandler(UI userInterface) throws UnknownHostException, IOException {
        super(userInterface);

        mainSocket = new Socket(Config.ServerIP, Config.MainSocketPort);

        this.outputMainStream = new ObjectOutputStream(mainSocket.getOutputStream());
        this.inputMainStream = new ObjectInputStream(mainSocket.getInputStream());

        new Thread(() -> {
            while(true) {
                try {
                    MainEvent event = (MainEvent) inputMainStream.readObject();

                    event.execute(userInterface);

                    // If the event is a gameConnectionEvent we intercept it and try to connect to the game server
                    // The result of the game connection attempt is notified to the client by calling the connectionToGameResult method
                    if(event instanceof GameConnectionEvent) {
                        GameConnectionEvent gameConnectionEvent = (GameConnectionEvent) event;

                        try {
                            this.gameSocket = new Socket();
                            gameSocket.connect(new InetSocketAddress(Config.ServerIP, gameSocket.getPort()), 1000);

                            this.outputGameStream = new ObjectOutputStream(gameSocket.getOutputStream());
                            this.inputGameStream = new ObjectInputStream(gameSocket.getInputStream());

                            // Send userinfo
                            UserInfo userInfo = userInterface.getUserInfo();
                            outputGameStream.writeObject(userInfo);
                            outputGameStream.reset();

                            connectedToGame = true;
                        } catch (SocketTimeoutException e) {
                            System.err.println("Failed to establish connection to game server: socket connection attempt timeout");
                            e.printStackTrace();
                        } catch (IOException e) {
                            System.err.println("Failed to start connection to the game server");
                            e.printStackTrace();
                        } finally {
                            userInterface.connectionToGameResult(connectedToGame);
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Failed to receive event");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to decode event");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean sendToMainServer(MainCommand command) {
        try {
            outputMainStream.writeObject(command);
            outputMainStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean sendToGameServer(GameCommand command) {
        if(!connectedToGame)
            return false;

        try {
            outputGameStream.writeObject(command);
            outputGameStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean reconnect() {
        // TODO
        return false;
    }


}
