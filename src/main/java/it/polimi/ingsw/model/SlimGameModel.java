package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.util.Trio;

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
    public final Map<PlayerToken, Trio<Integer, Integer, Integer>> tokenToHand;

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
    public final Pair<Integer, Integer> visibleResourceCardsList;

    /**
     * Structure to keep track of the ids of the cards in the visible gold card
     * list.
     */
    public final Pair<Integer, Integer> visibleGoldCardsList;

    /**
     * Map to keep track of the scoreboard.
     */
    public final Map<PlayerToken, Integer> scores;

    /**
     * @param tokenToPlayedCards       map to keep track of cards placement order
     * @param tokenToHand              map from token to cards in his hand
     * @param tokenToElements          map from token to his elements counts
     * @param tokenToSecretObjective   map from token to his secret objective
     * @param commonObjectives         list of common objectives
     * @param resourceDeck             list with ids of cards in the resource deck
     * @param goldDeck                 list with ids of cards in the gold deck
     * @param visibleResourceCardsList pair of the two cards in the visible resource
     *                                 cards list
     * @param visibleGoldCardsList     pair of the two cards in the visible gold
     *                                 cards list
     * @param scores                   map from token to his score
     */
    public SlimGameModel(
            Map<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> tokenToPlayedCards,
            Map<PlayerToken, Trio<Integer, Integer, Integer>> tokenToHand,
            Map<PlayerToken, Map<Elements, Integer>> tokenToElements,
            Map<PlayerToken, Integer> tokenToSecretObjective,
            List<Integer> commonObjectives,
            List<Integer> resourceDeck,
            List<Integer> goldDeck,
            Pair<Integer, Integer> visibleResourceCardsList,
            Pair<Integer, Integer> visibleGoldCardsList,
            Map<PlayerToken, Integer> scores) {
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
    }
}
