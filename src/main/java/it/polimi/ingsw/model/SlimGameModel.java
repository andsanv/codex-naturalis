package it.polimi.ingsw.model;

import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.distributed.events.game.PlayedCardEvent;
import it.polimi.ingsw.distributed.events.game.PlayerElementsEvent;
import it.polimi.ingsw.distributed.events.game.UpdatedScoreTrackEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Represents the model in a "slimmer" way.
 * Used when a client reconnects, after quitting the game, and asks to be updated on the game status
 */
public class SlimGameModel implements Serializable {
    /**
     * Map to keep track of the cards placement order, for every player
     */
    private final Map<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> tokenToPlayedCards;

    /**
     * Map that keeps track of items for every player
     */
    private final Map<PlayerToken, Map<Elements, Integer>> tokenToItems;

    /**
     * Map to keep track of the scoreboard
     */
    private final Map<PlayerToken, Integer> scores;


    public SlimGameModel() {
        this.tokenToPlayedCards = new HashMap<>();
        this.tokenToItems = new HashMap<>();
        this.scores = new HashMap<>();
    }

    /**
     * Constructor by copy. Will be used to send the slimGameModel to the reconnected clients
     * @param other the main slimGameModel in the gameModel
     */
    public SlimGameModel(SlimGameModel other) {
        this.tokenToPlayedCards = other.tokenToPlayedCards;
        this.tokenToItems = other.tokenToItems;
        this.scores = other.scores;
    }

    /**
     * Updates the map of a certain player, adding the newly placed card
     * @param event the event containing information on the player and the card
     */
    public void addPlayedCard(PlayedCardEvent event) {
        Optional<Integer> index = tokenToPlayedCards.get(event.senderToken).keySet().stream().max(Integer::compare);
        if(index.isPresent())
            tokenToPlayedCards.get(event.senderToken).put(index.get() + 1, new Trio<>(event.playedCardId, event.playedCardSide, event.playedCardCoordinates));
        else {
            HashMap<Integer, Trio<Integer, CardSide, Coords>> map = new HashMap<>();
            map.put(1, new Trio<>(event.playedCardId, event.playedCardSide, event.playedCardCoordinates));
            tokenToPlayedCards.put(event.senderToken, map);
        }
    }

    /**
     * Used to update playerItems after placing a card
     * @param event the event containing information on the playerItems update
     */
    public void updatePlayerItems(PlayerElementsEvent event) {
        tokenToItems.put(event.playerToken, new HashMap<>(event.resources));
    }

    /**
     * Used to update players' scores
     * @param event the event containing information on updated scores
     */
    public void updateScores(UpdatedScoreTrackEvent event) {
        scores.put(event.playerToken, event.score);
    }
}
