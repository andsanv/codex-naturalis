package it.polimi.ingsw.distributed.actions;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.client.ClientCard;
import it.polimi.ingsw.client.VirtualGameView;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public interface VirtualGameServer extends Remote {
    void placeCard(int lobbyId, UserInfo user, Coords coords, ClientCard card, CardSide cardSide)
            throws RemoteException;

    void drawResourceCard(UserInfo user) throws RemoteException;

    void drawGoldDeckCard(UserInfo user) throws RemoteException;

    void drawVisibleResourceCard(UserInfo user, int chosen) throws RemoteException;

    void drawVisibleGoldCard(UserInfo user, int chosen) throws RemoteException;

    void drawObjectiveCard(UserInfo user) throws RemoteException;

    void drawStarterCard(UserInfo user) throws RemoteException;

    void limitPointsReached(UserInfo user) throws RemoteException;

    void pickStarterCardSide(UserInfo user, CardSide side) throws RemoteException;

    void chooseToken(UserInfo user, PlayerToken token) throws RemoteException;

    void chooseObjective(UserInfo user, int objective) throws RemoteException;

    void connect(VirtualGameView clientGameView) throws RemoteException;
}
