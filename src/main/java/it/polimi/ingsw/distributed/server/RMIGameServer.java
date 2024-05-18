package it.polimi.ingsw.distributed.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.distributed.client.VirtualGameView;
import it.polimi.ingsw.distributed.commands.GameCommand;

public class RMIGameServer extends UnicastRemoteObject implements Runnable, VirtualGameServer {

    private final GameFlowManager gameFlowManager;

    public RMIGameServer(GameFlowManager gameFlowManager) throws RemoteException {
        this.gameFlowManager = gameFlowManager;
    }

    @Override
    public void send(GameCommand command) throws RemoteException {
        command.execute(gameFlowManager);
    }

    @Override
    public void connect(VirtualGameView clientGameView) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
