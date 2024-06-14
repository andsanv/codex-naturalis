package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.card.StarterCard;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Util class to make things easier when starting a match.
 * Used to create decks before gameModel is created.
 * Once the model is created, decks are transferred to the model, and this object is deleted.
 *
 * @see GameModel
 */
public class Decks {
    /**
     * ResourceCard cards deck.
     */
    public final Deck<ResourceCard> resourceCardsDeck;

    /**
     * GoldCard cards deck.
     */
    public final Deck<GoldCard> goldCardsDeck;

    /**
     * ObjectiveCard cards deck.
     */
    public final Deck<ObjectiveCard> objectiveCardsDeck;

    /**
     * StarterCard cards deck.
     */
    public final Deck<StarterCard> starterCardsDeck;

    public Decks(List<Observer> observers, AtomicInteger lastEventId) {
        this.objectiveCardsDeck = ObjectiveDeckCreator.createDeck(observers, lastEventId);
        this.starterCardsDeck = StarterDeckCreator.createDeck(observers, lastEventId);
        this.resourceCardsDeck = ResourceDeckCreator.createDeck(observers, lastEventId);
        this.goldCardsDeck = GoldDeckCreator.createDeck(observers, lastEventId);
    }
}
