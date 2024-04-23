package it.polimi.ingsw.network;

import it.polimi.ingsw.model.player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIRoomServerInterface extends RoomServer, Remote {

    void addPlayer(Player player) throws RemoteException;
    void startGame() throws RemoteException;
    List<GameServer> getServers() throws RemoteException;


}
