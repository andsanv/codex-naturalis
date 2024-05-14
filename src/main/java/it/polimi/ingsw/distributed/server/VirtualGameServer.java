package it.polimi.ingsw.distributed.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.distributed.client.VirtualGameView;
import it.polimi.ingsw.distributed.commands.GameCommand;

public interface VirtualGameServer extends Remote {
    public void send(GameCommand command) throws RemoteException;
    public void connect(VirtualGameView clientGameView) throws RemoteException;
}
