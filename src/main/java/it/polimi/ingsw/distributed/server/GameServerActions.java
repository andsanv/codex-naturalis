package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.GameCommand;

public interface GameServerActions extends Remote {
    public void send(GameCommand command) throws RemoteException;

    /**
     * This method must be called when connecting to the game server.
     * 
     * @param clientMainView the client's game view
     * @throws RemoteException
     */
    public void connectToGame(UserInfo userInfo, GameViewActions clientGameView) throws RemoteException;
}
