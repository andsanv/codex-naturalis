package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.client.ClientCard;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;

public interface GameActions{
    void placeCard(LobbyInfo lobby, String user, Coords coords, ClientCard card, CardSide cardSide)
            throws RemoteException;

    void drawResourceCard(LobbyInfo lobby, String user) throws RemoteException;

    void drawGoldDeckCard(LobbyInfo lobby, String user) throws RemoteException;

    void drawVisibleResourceCard(LobbyInfo lobby, String user, int chosen) throws RemoteException;

    void drawVisibleGoldCard(LobbyInfo lobby, String user, int chosen) throws RemoteException;

    void drawObjectiveCard(LobbyInfo lobby, String user) throws RemoteException;

    void drawStarterCard(LobbyInfo lobby, String user) throws RemoteException;

    void limitPointsReached(LobbyInfo lobby, String user) throws RemoteException;
}
