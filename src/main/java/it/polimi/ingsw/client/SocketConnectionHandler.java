package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;

public class SocketConnectionHandler extends ConnectionHandler {
    private Socket mainSocket;
    private Socket gameSocket;

    private ObjectOutputStream mainStream;
    private ObjectOutputStream gameStream;
    
    public SocketConnectionHandler() throws UnknownHostException, IOException {
        mainSocket = new Socket(Config.ServerIP, Config.MainSocketPort);
        this.mainStream = new ObjectOutputStream(mainSocket.getOutputStream());
    }

    @Override
    public boolean sendToMainServer(MainCommand command) {
        try {
            mainStream.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean sendToGameServer(GameCommand command) {
        try {
            gameStream.writeObject(command);
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
