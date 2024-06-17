package it.polimi.ingsw.model;

import it.polimi.ingsw.distributed.events.game.PlayedCardEvent;
import it.polimi.ingsw.model.card.CardSide;
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
     * List containing the ids of the common objectives.
     */
    public final List<Integer> commonObjectives;

    /**
     * Map that keeps the id of the private objective of a player, for every player.
     */
    public final Map<PlayerToken, Integer> tokenTosecretObjective;

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

        this.commonObjectives = new ArrayList<>(gameModel.commonObjectives.stream().map(x -> x.id).collect(Collectors.toList()));
        
        this.tokenTosecretObjective = new HashMap<>(
            playerTokens.stream().collect(Collectors.toMap(
                    token -> token,
                    token -> gameModel.tokenToPlayer.get(token).secretObjective.id
            ))
        );
    }

    /**
     * Constructor by copy. Will be used to send the slimGameModel to the reconnected clients
     * @param other the main slimGameModel in the gameModel
     */
    public SlimGameModel(SlimGameModel other) {
        this.scores = new HashMap<>(other.scores);
        this.commonObjectives = new ArrayList<>(other.commonObjectives);
        this.tokenTosecretObjective = new HashMap<>(other.tokenTosecretObjective);

        this.tokenToPlayedCards = new HashMap<PlayerToken, Map<Integer, Trio<Integer, CardSide, Coords>>> () {{
            other.tokenToPlayedCards.entrySet().stream().forEach(
                x -> put(x.getKey(), new HashMap<Integer, Trio<Integer, CardSide, Coords>>() {{
                    other.tokenToPlayedCards.get(x.getKey()).entrySet().stream().forEach(
                        y -> put(
                            y.getKey(), 
                            new Trio<>(
                                other.tokenToPlayedCards.get(x.getKey()).get(y.getKey()).first,
                                other.tokenToPlayedCards.get(x.getKey()).get(y.getKey()).second,
                                other.tokenToPlayedCards.get(x.getKey()).get(y.getKey()).third
                            )
                        )
                    );
                }})
            );
        }};
        
        this.tokenToItems = new HashMap<PlayerToken, Map<Elements, Integer>>() {{
            other.tokenToItems.entrySet().stream().forEach(
                x -> put(x.getKey(), new HashMap<>(other.tokenToItems.get(x.getKey())))
            );
        }};
    }

    /**
     * Updates the map of a certain player, adding the newly placed card
     * @param event the event containing information on the player and the card
     */
    public void addPlayedCard(PlayedCardEvent event) {
        Optional<Integer> index = tokenToPlayedCards.get(event.senderToken).keySet().stream().max(Integer::compare);
    
        tokenToPlayedCards.get(event.senderToken).put(index.orElseThrow() + 1, new Trio<>(event.playedCardId, event.playedCardSide, event.playedCardCoordinates));
    }
}
