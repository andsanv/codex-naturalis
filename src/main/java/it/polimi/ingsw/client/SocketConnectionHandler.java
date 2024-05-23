package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.GameCommand;
import it.polimi.ingsw.distributed.commands.ServerCommand;

import java.rmi.RemoteException;

public class SocketConnectionHandler extends ConnectionHandler {
    public SocketConnectionHandler() {

    }

    @Override
    public boolean sendToMainServer() {

    }

    @Override
    public boolean sendToGameServer() {

    }

    @Override
    public boolean reconnect() {}
}
