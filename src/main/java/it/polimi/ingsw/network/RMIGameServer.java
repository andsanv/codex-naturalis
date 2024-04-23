package it.polimi.ingsw.network;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.Player;

import java.rmi.RemoteException;
import java.util.List;

public class RMIGameServer implements RMIGameServerInterface{

    @Override
    public void startGame() {

    }

    @Override
    public void placeCard(Player player, Coords coords, PlayableCard card, CardSide cardSide) throws RemoteException {

    }

    @Override
    public void drawResourceCard(Player player) throws RemoteException {

    }

    @Override
    public void drawGoldDeckCard(Player player) throws RemoteException {

    }

    @Override
    public void drawVisibleResourceCard(Player player, int chosen) throws RemoteException {

    }

    @Override
    public void drawVisibleGoldCard(Player player, int chosen) throws RemoteException {

    }

    @Override
    public void drawObjectiveCard(Player player) throws RemoteException {

    }

    @Override
    public void drawStarterCard(Player player) throws RemoteException {

    }

    @Override
    public void limitPointsReached(Player player) throws RemoteException {

    }

    @Override
    public String getGameId() {
        return "";
    }

    @Override
    public List<Player> getPlayers() {
        return List.of();
    }
}
