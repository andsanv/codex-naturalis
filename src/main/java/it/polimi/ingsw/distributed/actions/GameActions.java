package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.client.ClientCard;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.events.Event;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public interface GameActions extends Remote {
    void placeCard(int lobbyId, UserInfo user, Coords coords, ClientCard card, CardSide cardSide)
            throws RemoteException;

    void drawResourceCard(int lobbyId, UserInfo user) throws RemoteException;

    void drawGoldDeckCard(int lobbyId, UserInfo user) throws RemoteException;

    void drawVisibleResourceCard(int lobbyId, UserInfo user, int chosen) throws RemoteException;

    void drawVisibleGoldCard(int lobbyId, UserInfo user, int chosen) throws RemoteException;

    void drawObjectiveCard(int lobbyId, UserInfo user) throws RemoteException;

    void drawStarterCard(int lobbyId, UserInfo user) throws RemoteException;

    void limitPointsReached(int lobbyId, UserInfo user) throws RemoteException;

    void pickStarterCardSide(int lobbyId, UserInfo user, CardSide side) throws RemoteException;

    void chooseToken(int lobbyId, UserInfo user, PlayerToken token) throws RemoteException;

    void chooseObjective(int lobbyId, UserInfo user, int objective) throws RemoteException;

    public Event getLastEvent() throws RemoteException;
}
