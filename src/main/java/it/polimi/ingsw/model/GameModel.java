package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the state of the game items.
 * There are no informations about players' turns and connections/disconnections, as they are handled in the controller package.
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

    private final AtomicInteger lastEventId;

    /**
     * After creating the GameModel, a TODO must be called.
     */
    public GameModel(AtomicInteger lastEventId) {
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

        this.lastEventId = lastEventId;

        scoreTrack = null;
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

    public void setCommonObjectives(List<ObjectiveCard> commonObjectives) {
        this.commonObjectives = commonObjectives;
    }

    public List<ObjectiveCard> getCommonObjectives() {
        return commonObjectives;
    }

    public void setScoreTrack(List<PlayerToken> playerTokens) {
        this.scoreTrack = new ScoreTrack(playerTokens);
    }
}
