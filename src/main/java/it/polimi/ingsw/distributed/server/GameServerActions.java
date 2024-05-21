package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.client.GameViewActions;
import it.polimi.ingsw.distributed.commands.game.GameCommand;

public interface GameServerActions extends Remote {
    public void send(GameCommand command) throws RemoteException;
    public void connect(UserInfo userInfo, GameViewActions clientGameView) throws RemoteException;
}
