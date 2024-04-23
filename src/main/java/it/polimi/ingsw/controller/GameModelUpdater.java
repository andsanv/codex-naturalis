package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.player.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles updates of the model
 */
public class GameModelUpdater {
    private GameModel model;

     public GameModelUpdater(GameModel model) {
        this.model = model;
    }

    /**
     * Method that plays the given card at the given coordinates
     *
     * @param coords coords at which to play the card
     * @param card   card to play
     * @return boolean based on whether the card was placed or not
     */
    public boolean playCard(PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerBoard playerBoard = player.getBoard();
        PlayerHand playerHand = player.getHand();

        if(!player.getBoard().canPlaceCardAt(coords, card, cardSide))
            return false;
        
        playerHand.removeCard(card);
        card.playSide(cardSide);
        playerBoard.setCard(coords, card);

        // playability is already checked, so no HIDDEN corners are present
        playerBoard.adjacentCorners(coords).values().forEach(corner -> corner.setType(CornerTypes.COVERED));

        playerBoard.updatePlayerItems(coords);

        int points;
        switch(card.getPointsType()) {
            case ONE:
                points = 1;
                break;
            case THREE:
                points = 3;
                break;
            case FIVE:
                points = 5;
                break;
            case ONE_PER_QUILL:
                points = playerBoard.getPlayerItems().get(Items.QUILL);
                break;
            case ONE_PER_INKWELL:
                points = playerBoard.getPlayerItems().get(Items.INKWELL);
                break;
            case ONE_PER_MANUSCRIPT:
                points = playerBoard.getPlayerItems().get(Items.MANUSCRIPT);
                break;
            case TWO_PER_COVERED_CORNER:
                points = 2 * playerBoard.adjacentCards(coords).keySet().size();
                break;
            default:
                points = 0;
                break;
        }

        model.getScoreTrack().updatePlayerScore(player, points);

        return true;
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
        List<ResourceCard> visibleResourceCard = model.getVisibleResourceCards();

        ResourceCard card = model.getVisibleResourceCards().get(chosen);

        if (card == null)
            return false;

        playerHand.addCard(card);

        Optional<ResourceCard> newCard = model.getResourceCardsDeck().draw();
        newCard.ifPresent(resourceCard -> visibleResourceCard.add(chosen, resourceCard));

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
        List<GoldCard> visibleGoldCards = model.getVisibleGoldCards();

        GoldCard card = visibleGoldCards.get(chosen);

        if (card == null)
            return false;

        playerHand.addCard(card);

        Optional<GoldCard> newCard = model.getGoldCardsDeck().draw();
        newCard.ifPresent(goldCard -> visibleGoldCards.add(chosen, goldCard));

        return true;
    }

    public Optional<ObjectiveCard> drawObjectiveCard() {
        return model.getObjectiveCardsDeck().draw();
    }

    public Optional<StarterCard> drawStarterCard() {
        return model.getStarterCardsDeck().draw();
    }

    public boolean limitPointsReached() {
        return model.getScoreTrack().isGameFinished();
    }

    public void addPlayer(PlayerToken token, StarterCard starterCard, CardSide starterCardSide, ObjectiveCard objectiveCard) {
        model.tokenToPlayer.put(token, new Player(starterCard, starterCardSide, objectiveCard));
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
