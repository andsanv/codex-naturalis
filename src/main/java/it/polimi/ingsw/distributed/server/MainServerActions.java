package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.client.MainViewActions;
import it.polimi.ingsw.distributed.commands.main.MainCommand;
import it.polimi.ingsw.controller.server.UserInfo;

public interface MainServerActions extends Remote {
    
    public void send(MainCommand command) throws RemoteException;

    /**
     * This method is used when connecting to the server with an already existing account.
     * The client can retrieve the UserInfo, for example, from a config file he created after connecting for the first time.
     *
     * @param userInfo the user information
     * @param clientMainView the client's main view
     * @throws RemoteException
     */
    void reconnect(UserInfo userInfo, MainViewActions clientMainView) throws RemoteException;

    /**
     * This method must be called when connecting to the server for the first time.
     *
     * @param userInfo the client's info
     * @param clientMainView the client's main view
     * @throws RemoteException
     */
    void connectToMain(UserInfo userInfo, MainViewActions clientMainView) throws RemoteException;
}
