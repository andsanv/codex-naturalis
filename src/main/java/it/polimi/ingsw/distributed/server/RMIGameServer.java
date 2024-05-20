package it.polimi.ingsw.distributed.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.GameCommand;

public class RMIGameServer extends UnicastRemoteObject implements Runnable, GameServerActions {

    private final GameFlowManager gameFlowManager;

    private ConcurrentHashMap<UserInfo, GameViewActions> clients;

    public RMIGameServer(GameFlowManager gameFlowManager) throws RemoteException {
        this.gameFlowManager = gameFlowManager;
    }

    @Override
    public void send(GameCommand command) throws RemoteException {
        gameFlowManager.addCommand(command);
    }

    @Override
    public void connect(UserInfo userInfo, GameViewActions clientGameView) throws RemoteException {
        clients.put(userInfo, clientGameView);
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
