package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.*;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.deck.VisibleCardsList;
import it.polimi.ingsw.model.player.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles updates of the model
 */
public class GameModelUpdater {
    private final GameModel model;
    private final AtomicInteger lastEventId;

    public GameModelUpdater(GameModel model, List<Observer> observers) {
        this.model = model;
        this.lastEventId = new AtomicInteger(1);

        Observable.setObservers(observers);
        Observable.setLastEventId(lastEventId);
    }

    /**
     * Method that plays the given card at the given coordinates
     *
     * @param coords coords at which to play the card
     * @param card card to play
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

        model.getScoreTrack().updatePlayerScore(playerToken, points);

        return true;
    }

    /**
     * @param playerToken the token of the player
     * @return true if the action has been completed successfully, false otherwise
     */
    public boolean drawResourceDeckCard(PlayerToken playerToken) {
        Player player = model.tokenToPlayer.get(playerToken);
        PlayerHand playerHand = player.getHand();

        if(playerHand.getCards().size() == 3)
            return false;

        Optional<ResourceCard> card = model.getResourceCardsDeck().draw();

        if (!card.isPresent())
            return false;
        else
            model.getResourceCardsDeck().notify(new DrawnResourceDeckCardEvent(playerToken, card.get().getId()));

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

        if(playerHand.getCards().size() == 3)
            return false;

        Optional<GoldCard> card = model.getGoldCardsDeck().draw();

        if (!card.isPresent())
            return false;
        else
            model.getGoldCardsDeck().notify(new DrawnGoldDeckCardEvent(playerToken, card.get().getId()));


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

        if(playerHand.getCards().size() == 3)
            return false;

        VisibleCardsList<ResourceCard> visibleResourceCards = model.getVisibleResourceCards();
        ResourceCard card = visibleResourceCards.get(chosen);

        if (card == null)
            return false;
        else
            visibleResourceCards.notify(new DrawnVisibleResourceCardEvent(playerToken, chosen, card.getId()));

        visibleResourceCards.remove(chosen);
        playerHand.addCard(card);

        Optional<ResourceCard> newCard = model.getResourceCardsDeck().draw();
        visibleResourceCards.add(chosen, newCard.orElse(null));
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

        if(playerHand.getCards().size() == 3)
            return false;

        VisibleCardsList<GoldCard> visibleGoldCards = model.getVisibleGoldCards();
        GoldCard card = visibleGoldCards.get(chosen);

        if (card == null)
            return false;
        else
            visibleGoldCards.notify(new DrawnVisibleGoldCardEvent(playerToken, chosen, card.getId()));

        visibleGoldCards.remove(chosen);
        playerHand.addCard(card);

        Optional<GoldCard> newCard = model.getGoldCardsDeck().draw();
        visibleGoldCards.add(chosen, newCard.orElse(null));

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

    /**
     * Checks whether one of the two decks is empty
     *
     * @return A boolean
     */
    public boolean someDecksEmpty() {
        return model.getResourceCardsDeck().isEmpty() || model.getGoldCardsDeck().isEmpty();
    }

    /**
     * Adds a player to the tokenToPlayer map in the model. Used in the setup phase of the game
     *
     * @param token PlayerToken chosen by the player
     * @param starterCard StarterCard drawn by the player
     * @param starterCardSide StarterCard chosen by the player
     * @param objectiveCard ObjectiveCard chosen by the player
     * @return A boolean that depends on whether the player was added or not
     */
    public boolean addPlayer(PlayerToken token, StarterCard starterCard, CardSide starterCardSide, ObjectiveCard objectiveCard) {
        if(model.tokenToPlayer.containsKey(token))
            return false;

        model.tokenToPlayer.put(token, new Player(starterCard, starterCardSide, objectiveCard));
        return true;
    }

    public Map<PlayerToken, Player> getPlayers() {
        return model.tokenToPlayer;
    }

    public void setCommonObjectives(List<ObjectiveCard> commonObjectives) {
        model.setCommonObjectives(commonObjectives);
    }

    public List<ObjectiveCard> getCommonObjectives() {
        return model.getCommonObjectives();
    }

    public void setScoreTrack(List<PlayerToken> playerTokens) {
        model.setScoreTrack(playerTokens);
    }

    public GameModel getModel() {
        return model;
    }

}
