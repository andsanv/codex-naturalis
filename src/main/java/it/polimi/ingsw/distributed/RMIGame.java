package it.polimi.ingsw.distributed;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.Config;
import it.polimi.ingsw.client.lightModel.ClientCard;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;

public class RMIGame extends UnicastRemoteObject implements GameActions, Runnable {
    public RMIGame() throws RemoteException {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(Config.RMIGamePort);
            registry.bind(Config.RMIGameName, this);
        } catch (RemoteException e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.err.println("Error: " + Config.RMIGameName + " already bound");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    @Override
    public void placeCard(LobbyInfo lobby, String user, Coords coords, ClientCard card, CardSide cardSide)
            throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'placeCard'");
    }

    @Override
    public void drawResourceCard(LobbyInfo lobby, String user) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawResourceCard'");
    }

    @Override
    public void drawGoldDeckCard(LobbyInfo lobby, String user) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawGoldDeckCard'");
    }

    @Override
    public void drawVisibleResourceCard(LobbyInfo lobby, String user, int chosen) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawVisibleResourceCard'");
    }

    @Override
    public void drawVisibleGoldCard(LobbyInfo lobby, String user, int chosen) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawVisibleGoldCard'");
    }

    @Override
    public void drawObjectiveCard(LobbyInfo lobby, String user) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawObjectiveCard'");
    }

    @Override
    public void drawStarterCard(LobbyInfo lobby, String user) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawStarterCard'");
    }

    @Override
    public void limitPointsReached(LobbyInfo lobby, String user) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'limitPointsReached'");
    }

}