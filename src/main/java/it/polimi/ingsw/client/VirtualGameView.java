package it.polimi.ingsw.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public interface VirtualGameView extends Remote {
    /**
     * Sets the score of the player to the given points
     * 
     * @param player       selected player
     * @param updatedScore the new score
     * @throws RemoteException
     */
    void updateScoreTrack(UserInfo player, int updatedScore) throws RemoteException;

    /**
     * Updates the game board of a player
     * 
     * @param player      selected player player
     * @param cardId      the id of the card
     * @param cardSide    the played side of the card
     * @param coordinates coordinates on the board of the played card
     * @throws RemoteException
     */
    void updatePlayerBoard(UserInfo player, int cardId, CardSide cardSide, Coords coordinates) throws RemoteException;

    /**
     * Adds a card to the given player's hand
     * 
     * @param player selected player
     * @param cardId id of the card to add
     * @throws RemoteException
     */
    void addToPlayerHand(UserInfo player, int cardId) throws RemoteException;

    /**
     * Removes a card from the given player's hand
     * 
     * @param player selected player
     * @param cardId id of the card to remove
     * @throws RemoteException
     */
    void removeFromPlayerHand(UserInfo player, int cardId) throws RemoteException;

    /**
     * Removes the top card of the resource deck
     * 
     * @throws RemoteException
     */
    void popResourceDeck() throws RemoteException;

    /**
     * Removes the top card of the gold deck
     * 
     * @throws RemoteException
     */
    void popGoldDeck() throws RemoteException;

    /**
     * Replaces a visible resource card with another one
     * 
     * @param cardId   the id of the new card
     * @param position the position of the old visible card
     * @throws RemoteException
     */
    void replaceVisibleResourceCard(int cardId, int position) throws RemoteException;

    /**
     * Replaces a visible gold card with another one
     * 
     * @param cardId   the id of the new card
     * @param position the position of the old visible card
     * @throws RemoteException
     */
    void replaceVisibleGoldCard(int cardId, int position) throws RemoteException;

    /**
     * Updates the token by a player
     * 
     * @param player selected player
     * @param token  the selected token
     * @throws RemoteException
     */
    void setToken(UserInfo player, PlayerToken token) throws RemoteException;

    /**
     * Gives a starter card to the current player
     * 
     * @param cardId the id of the starter card
     * @throws RemoteException
     */
    void giveStarterCard(int cardId) throws RemoteException;

    /**
     * Set common objective
     * 
     * @param firstObjectiveId  the id of the first objective card
     * @param secondObjectiveId the id of the second objective card
     * @throws RemoteException
     */
    void setCommonObjectives(int firstObjectiveId, int secondObjectiveId) throws RemoteException;

    /**
     * Set common objective
     * 
     * @param cardId the id of the objective card
     * @throws RemoteException
     */
    void setSecretObjective(int cardId) throws RemoteException;

    /**
     * Displays an error message.
     * This function is usually called when the client attemps an illegal move.
     * 
     * @param error error message to display
     * @throws RemoteException
     */
    void displayError(String error) throws RemoteException;

    /**
     * Sends a direct message to the player.
     * 
     * @param sender  the sender of the message
     * @param message the sent message
     * @throws RemoteException
     */
    void sendDirectMessage(UserInfo sender, String message) throws RemoteException;

    /**
     * Sends a message to the game's group chat
     * 
     * @param sender  the sender of the message
     * @param message the sent message
     * @throws RemoteException
     */
    void sendGameMessage(UserInfo sender, String message) throws RemoteException;
}
