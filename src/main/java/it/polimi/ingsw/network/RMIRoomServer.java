package it.polimi.ingsw.network;

import it.polimi.ingsw.model.player.Player;

import java.rmi.RemoteException;
import java.util.List;

public class RMIRoomServer implements RMIRoomServerInterface{
    @Override
    public void addPlayer(Player player) throws RemoteException {

    }

    @Override
    public void startGame() throws RemoteException {

    }

    @Override
    public List<GameServer> getServers() throws RemoteException {
        return List.of();
    }
}
