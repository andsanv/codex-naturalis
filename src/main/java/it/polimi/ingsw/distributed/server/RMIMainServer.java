package it.polimi.ingsw.distributed.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.VirtualMainView;

public class RMIMainServer extends UnicastRemoteObject implements VirtualMainServer, Runnable {

    private final ExecutorService executorService;

    public RMIMainServer() throws RemoteException {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(Config.RMIServerPort);
            registry.bind(Config.RMIServerName, this);
        } catch (RemoteException e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.err.println("Error: " + Config.RMIServerName + " already bound");
            e.printStackTrace();
        }
    }

    @Override
    public boolean joinLobby(UserInfo user, int lobbyId) {
        Callable<Boolean> callable = () -> Server.INSTANCE.joinLobby(user, lobbyId);
        Future<Boolean> future = executorService.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean leaveLobby(UserInfo user, int lobbyId) throws RemoteException {
        Callable<Boolean> callable = () -> Server.INSTANCE.leaveLobby(user, lobbyId);
        Future<Boolean> future = executorService.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserInfo signup(String name) throws RemoteException {
        Callable<UserInfo> callable = () -> Server.INSTANCE.signup(name);
        Future<UserInfo> future = executorService.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean startGame(UserInfo user, int lobbyId) throws RemoteException {
        Callable<Boolean> callable = () -> Server.INSTANCE.startGame(user, lobbyId);
        Future<Boolean> future = executorService.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void connect(VirtualMainView clientMainView) throws RemoteException {
        Callable<Void> callable = () -> {
            Server.INSTANCE.addConnectedClient(clientMainView);
            return null;
        };
        executorService.submit(callable);
    }

    @Override
    public LobbyInfo createLobby(UserInfo userInfo) throws RemoteException {
        Callable<LobbyInfo> callable = () -> Server.INSTANCE.createLobby(userInfo);
        Future<LobbyInfo> future = executorService.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}