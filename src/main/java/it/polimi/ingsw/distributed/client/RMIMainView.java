package it.polimi.ingsw.distributed.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.commands.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.SignUpCommand;
import it.polimi.ingsw.distributed.server.MainServerActions;

public class RMIMainView extends UnicastRemoteObject implements MainViewActions, Runnable {
    private UserInfo userInfo;
    private MainServerActions MainServerActions;
    private final GameEventHandler gameEventHandler;
    private final MainEventHandler mainEventHandler;

    // private final Object printLock;

    public RMIMainView(GameEventHandler gameEventHandler, MainEventHandler mainEventHandler) throws RemoteException {
        // this.printLock = printLock;
        this.userInfo = new UserInfo(new User("test"));
        this.gameEventHandler = gameEventHandler;
        this.mainEventHandler = mainEventHandler;
    }

    @Override
    public void receiveError(String error) throws RemoteException {
        mainEventHandler.handleErrorMessage(error);
    }

    @Override
    public void run() {
        Registry registry;
        try {
            // Connect to the server
            registry = LocateRegistry.getRegistry(Config.RMIServerPort);
            MainServerActions mainServerActions = (MainServerActions) registry.lookup(Config.RMIServerName);

            // Send the virtual view to the server
            mainServerActions.connect(this);

            // Create an user
            mainServerActions.send(new SignUpCommand("test"));

            // Create a lobby
            mainServerActions.send(new CreateLobbyCommand(userInfo));

            while (true) {
                Thread.sleep(1000);
            }

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error: failed to connect to the RMI server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public UserInfo getUserInfo() throws RemoteException {
        return userInfo;
    }

    @Override
    public void receiveLobbies(List<LobbyInfo> lobbies) throws RemoteException {
        mainEventHandler.handleLobbiesUpdate(lobbies);
    }
}
