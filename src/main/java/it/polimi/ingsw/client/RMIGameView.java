package it.polimi.ingsw.client;

import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public class RMIGameView implements VirtualGameView {
    public RMIGameView() throws RemoteException {
    }

    @Override
    public void updateScoreTrack(UserInfo player, int updatedScore) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateScoreTrack'");
    }

    @Override
    public void updatePlayerBoard(UserInfo player, int cardId, CardSide cardSide, Coords coordinates)
            throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePlayerBoard'");
    }

    @Override
    public void addToPlayerHand(UserInfo player, int cardId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToPlayerHand'");
    }

    @Override
    public void removeFromPlayerHand(UserInfo player, int cardId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeFromPlayerHand'");
    }

    @Override
    public void popResourceDeck() throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'popResourceDeck'");
    }

    @Override
    public void popGoldDeck() throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'popGoldDeck'");
    }

    @Override
    public void replaceVisibleResourceCard(int cardId, int position) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replaceVisibleResourceCard'");
    }

    @Override
    public void replaceVisibleGoldCard(int cardId, int position) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replaceVisibleGoldCard'");
    }

    @Override
    public void setToken(UserInfo player, PlayerToken token) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setToken'");
    }

    @Override
    public void giveStarterCard(int cardId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveStarterCard'");
    }

    @Override
    public void setCommonObjectives(int firstObjectiveId, int secondObjectiveId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCommonObjectives'");
    }

    @Override
    public void setSecretObjective(int cardId) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSecretObjective'");
    }

    @Override
    public void displayError(String error) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayError'");
    }

    @Override
    public void sendDirectMessage(UserInfo sender, String message) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendDirectMessage'");
    }

    @Override
    public void sendGameMessage(UserInfo sender, String message) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendGameMessage'");
    }
    
}
