package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.ServerCommand;

public interface MainServerActions extends Remote {
    
    public void send(ServerCommand command) throws RemoteException;

    /**
     * This method must be called when connecting to the server.
     * 
     * @param userInfo the user information
     * @param clientMainView the client's main view
     * @throws RemoteException
     */
    void connectToMain(UserInfo userInfo, MainViewActions clientMainView) throws RemoteException;
}
