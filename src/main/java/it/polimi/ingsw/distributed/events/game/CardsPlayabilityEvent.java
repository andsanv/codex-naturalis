package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.interfaces.GameEventHandler;

import java.util.List;
import java.util.Map;

/**
 * This event is sent in response to a CardsPlayabilityRequest.
 * Lets caller know at which coords the cards in his hands can be placed.
 * It is mainly used for UI reasons.
 */
public class CardsPlayabilityEvent extends GameEvent {

    /** Token of the player */
    public final PlayerToken playerToken;

    /** Map that track whether a card can be placed or not, both sides considered */
    public final Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability;

    /**
     * Tracks coordinates of slots on which cards can be played.
     * Coordinates do not depend on the card.
     */
    public final List<Coords> availableSlots;

    /**
     * This constructor creates the event starting from the player token, the
     * available slots and the cards playability.
     * 
     * @param playerToken      token of the player that requested the cards'
     *                         playability.
     * @param availableSlots   list of available slots.
     * @param cardsPlayability map that tracks whether a card can be placed or not,
     *                         for each side.
     */
    public CardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        this.playerToken = playerToken;
        this.cardsPlayability = cardsPlayability;
        this.availableSlots = availableSlots;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleCardsPlayabilityEvent(playerToken, availableSlots, cardsPlayability);
    }
}
