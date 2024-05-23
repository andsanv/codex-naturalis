package it.polimi.ingsw.distributed.server;

import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;

public class SocketGameServer implements GameServerActions, Runnable {

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    @Override
    public void send(GameCommand command) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    public void connectToGame(UserInfo userInfo, GameViewActions clientGameView) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }
    
}
