package it.polimi.ingsw.distributed;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.client.ClientCard;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.actions.RemoteActions;
import it.polimi.ingsw.distributed.actions.ServerActions;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;

public class RMIServer extends UnicastRemoteObject implements ServerActions, Runnable {
    public RMIServer() throws RemoteException {
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
    public List<LobbyInfo> getLobbies() throws RemoteException {
        return Server.INSTANCE.getLobbies();
    }

    @Override
    public boolean connectToGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.connectToGame(user, lobbyId);
    }

    @Override
    public boolean joinLobby(User user, int lobbyId) {
        return Server.INSTANCE.joinLobby(user, lobbyId);
    }

    @Override
    public boolean leaveLobby(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.leaveLobby(user, lobbyId);
    }

    @Override
    public UserInfo signup(String name, String password) throws RemoteException {
        return Server.INSTANCE.signup(name, password);
    }

    @Override
    public boolean startGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.startGame(user, lobbyId);
    }

    // @Override
    // public void placeCard(LobbyInfo lobby, String user, Coords coords, ClientCard card, CardSide cardSide)
    //         throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'placeCard'");
    // }

    // @Override
    // public void drawResourceCard(LobbyInfo lobby, String user) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawResourceCard'");
    // }

    // @Override
    // public void drawGoldDeckCard(LobbyInfo lobby, String user) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawGoldDeckCard'");
    // }

    // @Override
    // public void drawVisibleResourceCard(LobbyInfo lobby, String user, int chosen) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawVisibleResourceCard'");
    // }

    // @Override
    // public void drawVisibleGoldCard(LobbyInfo lobby, String user, int chosen) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawVisibleGoldCard'");
    // }

    // @Override
    // public void drawObjectiveCard(LobbyInfo lobby, String user) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawObjectiveCard'");
    // }

    // @Override
    // public void drawStarterCard(LobbyInfo lobby, String user) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'drawStarterCard'");
    // }

    // @Override
    // public void limitPointsReached(LobbyInfo lobby, String user) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'limitPointsReached'");
    // }

    // @Override
    // public Event getLastEvent() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getLastEvent'");
    // }

}