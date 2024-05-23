package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.commands.main.MainCommand;

import java.rmi.RemoteException;

public class SocketConnectionHandler extends ConnectionHandler {
    public SocketConnectionHandler() {

    }

    @Override
    public boolean sendToMainServer(MainCommand command) {
        // TODO
        return false;
    }

    @Override
    public boolean sendToGameServer(GameCommand command) {
        // TODO
        return false;
    }

    @Override
    public boolean reconnect() {
        // TODO
        return false;
    }
}
