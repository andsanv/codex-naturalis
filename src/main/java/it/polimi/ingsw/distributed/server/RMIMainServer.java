package it.polimi.ingsw.distributed.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.server.ServerCommand;

public class RMIMainServer extends UnicastRemoteObject implements MainServerActions, Runnable {

    private final ExecutorService executorService;

    public RMIMainServer() throws RemoteException {
        executorService = Executors.newCachedThreadPool();
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
    public void connect(MainViewActions clientMainView) throws RemoteException {
        Runnable task = () -> {
            Server.INSTANCE.addConnectedClient(clientMainView);
        };
        executorService.submit(task);
    }

	@Override
	public void send(ServerCommand command) throws RemoteException {
		executorService.submit(()-> {
            command.execute();
        });
    }
        
}