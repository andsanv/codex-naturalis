package it.polimi.ingsw.model;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;
import java.util.*;

/**
 * This class contains the state of the game items. There are no informations about players' turns
 * and connections/disconnections, as they are handled in the controller package.
 */
public class GameModel {
  public final Map<PlayerToken, Player> tokenToPlayer;

  private Deck<ResourceCard> resourceCardsDeck;
  private final VisibleCardsList<ResourceCard> visibleResourceCards;

  private Deck<GoldCard> goldCardsDeck;
  private VisibleCardsList<GoldCard> visibleGoldCards;

  private Deck<ObjectiveCard> objectiveCardsDeck;
  private Deck<StarterCard> starterCardsDeck;

  private List<ObjectiveCard> commonObjectives;

  private ScoreTrack scoreTrack;

  /**
   * A "simpler" model, that will be sent to clients during their reconnection phase
   */
  public SlimGameModel slimGameModel;

  /** After creating the GameModel, a TODO must be called. */
  public GameModel() {
    objectiveCardsDeck = ObjectiveDeckCreator.createDeck();
    starterCardsDeck = StarterDeckCreator.createDeck();
    resourceCardsDeck = ResourceDeckCreator.createDeck();
    goldCardsDeck = GoldDeckCreator.createDeck();

    visibleResourceCards =
        new VisibleCardsList<>(
            Arrays.asList(resourceCardsDeck.draw().get(), resourceCardsDeck.draw().get()));
    visibleGoldCards =
        new VisibleCardsList<>(
            Arrays.asList(goldCardsDeck.draw().get(), goldCardsDeck.draw().get()));

    tokenToPlayer = new HashMap<>();

    slimGameModel = new SlimGameModel();

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
  public VisibleCardsList<ResourceCard> getVisibleResourceCards() {
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
  public VisibleCardsList<GoldCard> getVisibleGoldCards() {
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
  }
  ;

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
