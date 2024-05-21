package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.server.ServerCommand;

// TODO replace user with userinfo (update methods in server to take UserInfo)
public interface MainServerActions extends Remote {

    public void send(ServerCommand command) throws RemoteException;

    /**
     * This method must be called when connecting to the server.
     * 
     * @param clientMainView the client's main view
     * @throws RemoteException
     */
    void connect(MainViewActions clientMainView) throws RemoteException;
}
