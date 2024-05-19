package it.polimi.ingsw.distributed.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.Config;
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
        /*Registry registry;

        try {
            registry = LocateRegistry.createRegistry(Config.RMIGameServerPort);
            registry.bind(Config.RMIServerName, this);
        } catch (RemoteException e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.err.println("Error: " + Config.RMIServerName + " already bound");
            e.printStackTrace();
        }*/
    }

}
