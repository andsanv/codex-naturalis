package model;

import model.card.CardSide;
import model.common.Elements;
import model.player.Coords;
import model.player.PlayerToken;
import util.Pair;
import util.Trio;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents the model in a "slimmer" way.
 * Used when after the initialization phase of the game and when a client
 * reconnects, after quitting the game, and asks to be updated on the game
 * status.
 * A new SlimGameModel object is built every time a clients asks for a
 * reconnection.
 *
 * @see GameModel
 */
public class SlimGameModel implements Serializable {
    /**
     * Map to keep track of the cards placement order, for every player.
     */
    public final Map<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> tokenToPlayedCards;

    /**
     * Map to keep track of each player's hand.
     */
    public final Map<PlayerToken, List<Integer>> tokenToHand;

    /**
     * Map that keeps track of elements for every player.
     */
    public final Map<PlayerToken, Map<Elements, Integer>> tokenToElements;

    /**
     * Map that keeps the id of the private objective of a player, for every player.
     */
    public final Map<PlayerToken, Integer> tokenToSecretObjective;

    /**
     * List containing the ids of the common objectives.
     */
    public final List<Integer> commonObjectives;

    /**
     * The resource deck as a list of ids, where the first one belongs to
     * the card at the bottom of the deck.
     */
    public final List<Integer> resourceDeck;

    /**
     * The gold deck as a list of ids, where the first one belongs to the
     * card at the bottom of the deck.
     */
    public final List<Integer> goldDeck;

    /**
     * Structure to keep track of the ids of the cards in the visible resource card
     * list.
     */
    public final List<Integer> visibleResourceCardsList;

    /**
     * Structure to keep track of the ids of the cards in the visible gold card
     * list.
     */
    public final List<Integer> visibleGoldCardsList;

    /**
     * Map to keep track of the scoreboard.
     */
    public final Map<PlayerToken, Integer> scores;

    /**
     * Tracks coordinates of slots on which cards can be played for each player.
     * Coordinates do not depend on the card.
     */
    public final Map<PlayerToken, List<Coords>> availableSlots;

    /**
     * Map that track whether a card can be placed or not, both sides considered for
     * each player.
     */
    public final Map<PlayerToken, Map<Integer, List<Pair<CardSide, Boolean>>>> cardsPlayability;

    /**
     * @param tokenToPlayedCards       map to keep track of cards placement order
     * @param tokenToHand              map from token to cards in his hand
     * @param tokenToElements          map from token to his elements counts
     * @param tokenToSecretObjective   map from token to his secret objective
     * @param commonObjectives         list of common objectives
     * @param resourceDeck             list with ids of cards in the resource deck
     * @param goldDeck                 list with ids of cards in the gold deck
     * @param visibleResourceCardsList list of the two cards in the visible resource
     *                                 cards list
     * @param visibleGoldCardsList     list of the two cards in the visible gold
     *                                 cards list
     * @param scores                   map from token to his score
     * @param availableSlots           available cards slots for each player
     * @param cardsPlayability         cards playability for each player
     */
    public SlimGameModel(
            Map<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> tokenToPlayedCards,
            Map<PlayerToken, List<Integer>> tokenToHand,
            Map<PlayerToken, Map<Elements, Integer>> tokenToElements,
            Map<PlayerToken, Integer> tokenToSecretObjective,
            List<Integer> commonObjectives,
            List<Integer> resourceDeck,
            List<Integer> goldDeck,
            List<Integer> visibleResourceCardsList,
            List<Integer> visibleGoldCardsList,
            Map<PlayerToken, Integer> scores,
            Map<PlayerToken, List<Coords>> availableSlots,
            Map<PlayerToken, Map<Integer, List<Pair<CardSide, Boolean>>>> cardsPlayability) {
        this.tokenToPlayedCards = tokenToPlayedCards;
        this.tokenToHand = tokenToHand;
        this.tokenToElements = tokenToElements;
        this.tokenToSecretObjective = tokenToSecretObjective;
        this.commonObjectives = commonObjectives;
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
        this.visibleResourceCardsList = visibleResourceCardsList;
        this.visibleGoldCardsList = visibleGoldCardsList;
        this.scores = scores;
        this.availableSlots = availableSlots;
        this.cardsPlayability = cardsPlayability;
    }

    /**
     * Applies the event of card playability to the slim model.
     * 
     * @param playerToken      the player token
     * @param availableSlots   the available slots
     * @param cardsPlayability the playability of each card
     */
    public void applyCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        this.availableSlots.put(playerToken, availableSlots);
        this.cardsPlayability.put(playerToken, cardsPlayability);
    }

    /**
     * Applies the PlayedCardEvent.
     *
     * @param playerToken the token of the player
     * @param cardId      id of the card played
     * @param cardSide    side of the card played
     * @param coords      coordinates where the card was placed
     */
    public void applyPlayedCardEvent(PlayerToken playerToken, int cardId, CardSide cardSide, Coords coords) {
        Map<Integer, Trio<Integer, CardSide, Coords>> playedCards = tokenToPlayedCards.get(playerToken);

        Integer lastId = playedCards.keySet().stream()
                .max(Integer::compare)
                .orElse(0);

        tokenToHand.get(playerToken).set(tokenToHand.get(playerToken).indexOf(Integer.valueOf(cardId)), null);

        playedCards.put(lastId + 1, new Trio<>(cardId, cardSide, coords));
    }

    /**
     * Applies the DrawnGoldDeckCardEvent
     *
     * @param playerToken token of the player
     * @param drawnCardId id of the drawn card
     * @param deckEmptied boolean that represents whether the deck was emptied or
     *                    not
     * @param nextCardId  id of the next card at the top of the deck
     * @param handIndex   index of the hand where card was drawn
     */
    public void applyDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        tokenToHand.get(playerToken).set(handIndex, drawnCardId);

        goldDeck.remove(goldDeck.indexOf(drawnCardId));
    }

    /**
     * Applies the DrawnResourceDeckCardEvent
     *
     * @param playerToken token of the player
     * @param drawnCardId id of the drawn card
     * @param deckEmptied boolean that represents whether the deck was emptied or
     *                    not
     * @param nextCardId  id of the next card at the top of the deck
     * @param handIndex   index of the hand where card was drawn
     */
    public void applyDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        tokenToHand.get(playerToken).set(handIndex, drawnCardId);

        resourceDeck.remove(resourceDeck.indexOf(drawnCardId));
    }

    /**
     * Applies the DrawnVisibleGoldCardEvent
     *
     * @param playerToken       the token of the player that draws the card.
     * @param drawnCardPosition the drawn card position in the visible resource
     *                          cards.
     * @param drawnCardId       the id of the drawn card.
     * @param replacementCardId the id of the replacement card.
     * @param deckEmptied       true if deck was emptied drawing the replacement
     *                          card, false otherwise.
     * @param nextCardId        id of the next card.
     * @param handIndex         index of the playerHand where drawn card will be
     *                          placed.
     */
    public void applyDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        tokenToHand.get(playerToken).set(handIndex, drawnCardId);

        visibleGoldCardsList.set(drawnCardPosition, replacementCardId);

        goldDeck.remove((Integer) replacementCardId);
    }

    /**
     * Applies the DrawnVisibleResourceCardEvent
     *
     * @param playerToken       the token of the player that draws the card.
     * @param drawnCardPosition the drawn card position in the visible resource
     *                          cards.
     * @param drawnCardId       the id of the drawn card.
     * @param replacementCardId the id of the replacement card.
     * @param deckEmptied       true if deck was emptied drawing the replacement
     *                          card, false otherwise.
     * @param nextCardId        id of the next card.
     * @param handIndex         index of the playerHand where drawn card will be
     *                          placed.
     */
    public void applyDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        tokenToHand.get(playerToken).set(handIndex, drawnCardId);

        visibleResourceCardsList.set(drawnCardPosition, replacementCardId);

        resourceDeck.remove((Integer) replacementCardId);
    }

    /**
     * Applies the UpdatedScoreTrackEvent.
     *
     * @param playerToken token of the player
     * @param score       the new player score
     */
    public void applyUpdatedScoreTrackEvent(PlayerToken playerToken, int score) {
        scores.put(playerToken, score);
    }

    /**
     * Applies the PlayerElementsEvent
     *
     * @param playerToken token of the player
     * @param resources   the updated resources map
     */
    public void applyPlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        tokenToElements.get(playerToken).clear();
        tokenToElements.get(playerToken).putAll(resources);
    }
}
