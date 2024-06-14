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
     * Map that track whether a card can be placed or not, both sides considered
     */
    public final Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability;

    /**
     * Tracks coordinates of slots on which cards can be played
     * Coordinates do not depend on the card
     */
    public final List<Coords> availableSlots;

    public CardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots, Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        this.playerToken = playerToken;
        this.cardsPlayability = cardsPlayability;
        this.availableSlots = availableSlots;
    }

    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleCardsPlayabilityEvent(playerToken, availableSlots, cardsPlayability);
    }
}
