package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.player.*;

import java.util.Map;
import java.util.Optional;

/**
 * Handles updates of the model
 */
public class GameModelUpdater {
    private GameModel model;

    GameModelUpdater(GameModel model) {
        this.model = model;
    }

    /**
     * Method that plays the given card at the given coordinates
     *
     * @param coords coords at which to play the card
     * @param card   card to play
     * @return boolean based on whether the card was placed or not
     */
    public boolean playCard(PlayerToken playerToken, Coords coords, PlayableCard card) {
        // TODO update scores, player's resources, etc

        if (model.tokenToPlayer.get(playerToken).getBoard().canPlaceCardAt(coords, card)) {
            model.tokenToPlayer.get(playerToken).getBoard().setCard(coords, card);
            return true;
        }

        return false;
    }

    /**
     * @param playerToken the token of the player
     * @return true if the action has been completed successfully, false otherwise
     */
    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerHand playerHand = player.getHand();

        Optional<ResourceCard> card = model.getResourceCardsDeck().draw();

        if (!card.isPresent())
            return false;

        playerHand.addCard(card.get());
        return true;
    }

    /**
     * @param playerToken the token of the player
     * @return true if the action has been completed successfully, false otherwise
     */
    public boolean drawGoldDeckCard(PlayerToken playerToken) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerHand playerHand = player.getHand();

        Optional<GoldCard> card = model.getGoldCardsDeck().draw();

        if (!card.isPresent())
            return false;

        playerHand.addCard(card.get());
        return true;
    }

    /**
     * @param playerToken the token of the player
     * @param chosen the card to draw from visible resource cards (0 or 1)
     * @return true if the action has been completed successfully, false otherwise
     */
    public boolean drawVisibleResourceCard(PlayerToken playerToken, int chosen) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerHand playerHand = player.getHand();

        ResourceCard card = model.getVisibleResourceCards().get(chosen);

        if (card == null)
            return false;

        playerHand.addCard(card);

        Optional<ResourceCard> newCard = model.getResourceCardsDeck().draw();

        newCard.ifPresent(playerHand::addCard);

        return true;
    }

    /**
     * @param playerToken the token of the player
     * @param chosen the card to draw from visible gold cards (0 or 1)
     * @return true if the action has been completed successfully, false otherwise
     */
    public boolean drawVisibleGoldCard(PlayerToken playerToken, int chosen) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerHand playerHand = player.getHand();

        GoldCard card = model.getVisibleGoldCards().get(chosen);

        if (card == null)
            return false;

        playerHand.addCard(card);

        Optional<GoldCard> newCard = model.getGoldCardsDeck().draw();

        newCard.ifPresent(playerHand::addCard);

        return true;
    }

    public Optional<ObjectiveCard> drawObjectiveCard() {
        return model.getObjectiveCardsDeck().draw();
    }

    public boolean limitPointsReached() {
        return model.getScoreTrack().isGameFinished();
    }

    /**
     * TODO PROBABLY GOES INTO GAMEFLOWMANAGER
     * This function must be called after players decide how to play their starter
     * card
     *
     * @param tokenToStarterCard a map from player tokens to their starter card
     * @param cardsSidesPlayed ...
     */
    public void setPlayersMap(Map<PlayerToken, StarterCard> tokenToStarterCard,
                              Map<StarterCard, CardSide> cardsSidesPlayed) {
        // TODO this function must be called only once, after calling method
        // getStarterCards()

        // TODO create here players and tokenToPlayer map

        // TODO randomly choose players' order (maybe not needed)
    }


}
