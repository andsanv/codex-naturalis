package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;

import java.util.List;
import java.util.Map;

/**
 * TODO: add javadoc
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

     private final List<PlayerToken> playersOrder;
     private PlayerToken currentPlayer;

    GameModel() {
        // TODO
        tokenToPlayer = null;
         playersOrder = null;
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

}
