package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Event thrown in response to a CardsPlayabilityCommand
 * Lets caller know at which coords the cards in his hands can be placed
 * Mainly used for UI reasons
 */
public class CardsPlayabilityEvent extends GameEvent {
    /**
     * Token of the player
     */
    public final PlayerToken playerToken;

    /**
     * Map where keys are cardIds, and each value is a pair of CardSide and list of coordinates at which that side of the card can be placed
     */
    public final Map<Integer, Pair<CardSide, List<Coords>>> cardsPlayability;

    public CardsPlayabilityEvent(PlayerToken playerToken, Map<Integer, Pair<CardSide, List<Coords>>> cardsPlayability) {
        this.playerToken = playerToken;
        this.cardsPlayability = cardsPlayability;
    }

    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleCardsPlayabilityEvent(playerToken, cardsPlayability);
    }
}
