package it.polimi.ingsw.network;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIGameServerInterface extends GameServer, Remote {
    void addPlayer(Player player);
    void startGame();

    // Game functionalities
    @Override
    void placeCard(Player player, Coords coords, PlayableCard card, CardSide cardSide) throws RemoteException;

    @Override
    void drawResourceCard(Player player) throws RemoteException;

    @Override
    void drawGoldDeckCard(Player player) throws RemoteException;

    @Override
    void drawVisibleResourceCard(Player player, int chosen) throws RemoteException;

    @Override
    void drawVisibleGoldCard(Player player, int chosen) throws RemoteException;

    @Override
    void drawObjectiveCard(Player player) throws RemoteException;

    @Override
    void drawStarterCard(Player player) throws RemoteException;

    @Override
    void limitPointsReached(Player player) throws RemoteException;

    // Informations
    String getGameId();
    List<Player> getPlayers();
}