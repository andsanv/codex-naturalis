package it.polimi.ingsw.model;

import it.polimi.ingsw.distributed.events.game.PlayedCardEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Represents the model in a "slimmer" way.
 * Used when a client reconnects, after quitting the game, and asks to be updated on the game status.
 * Every attribute points to an actual attribute of the GameModel.
 * A copy of the main SlimGameModel is sent at every reconnection request.
 *
 * @see GameModel
 */
public class SlimGameModel implements Serializable {
    /**
     * Map to keep track of the cards placement order, for every player.
     */
    public final Map<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> tokenToPlayedCards;

    /**
     * Map that keeps track of items for every player.
     */
    public final Map<PlayerToken, Map<Elements, Integer>> tokenToItems;

    /**
     * Map to keep track of the scoreboard.
     */
    public final Map<PlayerToken, Integer> scores;

    /**
     * List of common objectives.
     */
    public final List<ObjectiveCard> commonObjectives;

    /**
     * List of private objectives.
     */
    public final Map<PlayerToken, ObjectiveCard> secretObjectives;

    /**
     * @param gameModel GameModel used for initialization
     * @param playerTokens tokens of playing players
     * @param tokenToStarterCard map from token to chosen starter card
     * @param tokenToCardSide map from token to chosen card side
     * @param initialScores map from token to initial scores (all zero)
     */
    public SlimGameModel(
            GameModel gameModel,
            List<PlayerToken> playerTokens,
            Map<PlayerToken, StarterCard> tokenToStarterCard,
            Map<PlayerToken, CardSide> tokenToCardSide,
            Map<PlayerToken, Integer> initialScores
    ) {
        this.tokenToPlayedCards = new HashMap<>(
                playerTokens.stream()
                        .collect(Collectors.toMap(
                                token -> token,
                                token -> {
                                    Map<Integer, Trio<Integer, CardSide, Coords>> map = new HashMap<>();
                                    map.put(0, new Trio<>(tokenToStarterCard.get(token).id, tokenToCardSide.get(token), new Coords(0,0)));
                                    return map;
                                }
                                )
                        )
        );

        this.tokenToItems = new HashMap<>(
                playerTokens.stream()
                        .collect(Collectors.toMap(
                                        token -> token,
                                        token -> gameModel.tokenToPlayer.get(token).playerBoard.playerElements
                                )
                        )
        );

        this.scores = initialScores;

        this.commonObjectives = gameModel.commonObjectives;
        this.secretObjectives = new HashMap<>(
            playerTokens.stream().collect(Collectors.toMap(
                    token -> token,
                    token -> gameModel.tokenToPlayer.get(token).secretObjective
            ))
        );
    }

    /**
     * Constructor by copy. Will be used to send the slimGameModel to the reconnected clients
     * @param other the main slimGameModel in the gameModel
     */
    public SlimGameModel(SlimGameModel other) {
        this.tokenToPlayedCards = new HashMap<>(other.tokenToPlayedCards);
        this.tokenToItems = new HashMap<>(other.tokenToItems);
        this.scores = new HashMap<>(other.scores);
        this.commonObjectives = new ArrayList<>(other.commonObjectives);
        this.secretObjectives = new HashMap<>(other.secretObjectives);
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
}
