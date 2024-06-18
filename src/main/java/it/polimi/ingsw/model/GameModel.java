package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.deck.*;
import it.polimi.ingsw.model.player.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class contains the state of the game items.
 * There is no information about players' turns and connections/disconnections, as they are handled in the controller package.
 *
 * @see GameModelUpdater
 */
public class GameModel {
  /**
   * Maps a token to its corresponding player object.
   */
  public final Map<PlayerToken, Player> tokenToPlayer;

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

  /**
   * List of visible ResourceCard cards.
   */
  public final VisibleCardsList<ResourceCard> visibleResourceCards;

  /**
   * List of visible GoldCard cards.
   */
  public final VisibleCardsList<GoldCard> visibleGoldCards;

  /**
   * List of common objectives.
   */
  public final List<ObjectiveCard> commonObjectives;

  /**
   * Score track of the game
   */
  public final ScoreTrack scoreTrack;

  /**
   * Model is only created after all players chose their cards.
   *
   * @param decks decks to be used during game phase
   * @param playerTokens tokens playing
   * @param tokenToStarterCard map from token to the StarterCard card chosen
   * @param tokenToCardSide map from token to the CardSide chosen
   * @param tokenToObjectiveCard map from token to the ObjectiveCard card chosen
   * @param commonObjectives common objectives drawn
   * @param observers list of observers
   * @param lastEventId integer used to uniquely identify events
   */
  public GameModel(
          Decks decks,
          List<PlayerToken> playerTokens,
          Map<PlayerToken, StarterCard> tokenToStarterCard,
          Map<PlayerToken, CardSide> tokenToCardSide,
          Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard,
          List<ObjectiveCard> commonObjectives,
          List<Observer> observers,
          AtomicInteger lastEventId
  ) {
    // initialize decks
    this.objectiveCardsDeck = decks.objectiveCardsDeck;
    this.starterCardsDeck = decks.starterCardsDeck;
    this.resourceCardsDeck = decks.resourceCardsDeck;
    this.goldCardsDeck = decks.goldCardsDeck;

    this.visibleResourceCards =
        new VisibleCardsList<>(resourceCardsDeck, observers, lastEventId);
    this.visibleGoldCards =
        new VisibleCardsList<>(goldCardsDeck, observers, lastEventId);

    // set up game related objects
    Map<PlayerToken, List<PlayableCard>> tokenToInitialCards = new HashMap<>(
      playerTokens.stream().collect(Collectors.toMap(
              token -> token,
              token -> {
                List<PlayableCard> cards = new ArrayList<>(Arrays.asList(
                        resourceCardsDeck.anonymousDraw().first.orElseThrow(),
                        resourceCardsDeck.anonymousDraw().first.orElseThrow(),
                        goldCardsDeck.anonymousDraw().first.orElseThrow()
                ));
                Collections.shuffle(cards);
                return cards;
              }
              )
      )
    );

    this.tokenToPlayer = playerTokens.stream()
            .collect(Collectors.toMap(
                    token -> token,
                    token -> new Player(
                            tokenToStarterCard.get(token),
                            tokenToCardSide.get(token),
                            tokenToObjectiveCard.get(token),
                            tokenToInitialCards.get(token),
                            observers,
                            lastEventId
                    )
            ));

    Map<PlayerToken, Integer> initialScores = playerTokens.stream()
            .collect(Collectors.toMap(
                    token -> token,
                    token -> 0
                    ));

    this.scoreTrack = new ScoreTrack(initialScores, observers, lastEventId);

    this.commonObjectives = new ArrayList<>(commonObjectives);
  }
}
