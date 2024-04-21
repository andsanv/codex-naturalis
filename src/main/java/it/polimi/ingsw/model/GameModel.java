package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;

import java.util.*;
/**
 * TODO
 */
public class GameModel {
    public final Map<PlayerToken, Player> tokenToPlayer;

    private Deck<ResourceCard> resourceCardsDeck;
    private List<ResourceCard> visibleResourceCards;

    private Deck<GoldCard> goldCardsDeck;
    private List<GoldCard> visibleGoldCards;

    private Deck<ObjectiveCard> objectiveCardsDeck;
    private Deck<StarterCard> starterCardsDeck;

    private List<ObjectiveCard> commonObjectives;

    private ScoreTrack scoreTrack;

    /**
     * After creating the GameModel, a TODO must be called.
     */
    public GameModel() {
        objectiveCardsDeck = ObjectiveDeckCreator.createDeck();
        starterCardsDeck = StarterDeckCreator.createDeck();
        resourceCardsDeck = ResourceDeckCreator.createDeck();
        goldCardsDeck = GoldDeckCreator.createDeck();

        visibleGoldCards = new ArrayList<>();
        visibleGoldCards.add(goldCardsDeck.draw().get());
        visibleGoldCards.add(goldCardsDeck.draw().get());

        visibleResourceCards = new ArrayList<>();
        visibleResourceCards.add(resourceCardsDeck.draw().get());
        visibleResourceCards.add(resourceCardsDeck.draw().get());

        tokenToPlayer = new HashMap<>();
    }

    /**
     * @return the resource cards deck
     */
    public Deck<ResourceCard> getResourceCardsDeck() {
        return resourceCardsDeck;
    }

    /**
     * @return the visible resource cards list
     */
    public List<ResourceCard> getVisibleResourceCards() {
        return visibleResourceCards;
    }

    /**
     * @return the gold cards deck
     */
    public Deck<GoldCard> getGoldCardsDeck() {
        return goldCardsDeck;
    }

    /**
     * @return the visible gold cards list
     */
    public List<GoldCard> getVisibleGoldCards() {
        return visibleGoldCards;
    }

    /**
     * @return the objective cards deck
     */
    public Deck<ObjectiveCard> getObjectiveCardsDeck() {
        return objectiveCardsDeck;
    }

    /**
     * @return the game's score track
     */
    public ScoreTrack getScoreTrack() {
        return scoreTrack;
    }

    public Deck<StarterCard> getStarterCardsDeck() {
        return starterCardsDeck;
    };
}
