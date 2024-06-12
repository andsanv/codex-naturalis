package it.polimi.ingsw.controller.states;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import java.util.List;
import java.util.Map;

/**
 * A GameState refers to a state of the gameFlowManager (state machine), implemented using the state pattern.
 * GameState is an abstract class, from which the actual state classes inherit the methods that will eventually be re-implemented
 */
public abstract class GameState {
  /**
   * The state machine
   */
  protected GameFlowManager gameFlowManager;

  /**
   * Part of the controller which updates the model
   */
  protected GameModelUpdater gameModelUpdater;

  /**
   * list of players in the game
   */
  protected List<User> users;

  /**
   * GameState constructor
   *
   * @param gameFlowManager the object containing the state machine
   */
  GameState(GameFlowManager gameFlowManager) {
    this.gameFlowManager = gameFlowManager;
    this.gameModelUpdater = gameFlowManager.gameModelUpdater;
    this.users = gameFlowManager.users;
  }


  // PlayCardState methods

  public boolean playCard(
      PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
    return false;
  }

  public boolean getCardsPlayability(PlayerToken playerToken) {
    return false;
  }


  // DrawCardState methods

  public boolean drawResourceDeckCard(PlayerToken playerToken) {
    return false;
  }

  public boolean drawGoldDeckCard(PlayerToken playerToken) {
    return false;
  }

  public boolean drawVisibleResourceCard(PlayerToken playerToken, int choice) {
    return false;
  }

  public boolean drawVisibleGoldCard(PlayerToken playerToken, int choice) {
    return false;
  }


  // TokenSelectionState methods

  public Map<String, PlayerToken> handleTokenSelection(List<PlayerToken> playerTokens) {
    return null;
  }

  public boolean selectToken(UserInfo player, PlayerToken playerToken) {
    return false;
  }


  // StarterCardSelectionState methods

  public Pair<Map<PlayerToken, StarterCard>, Map<PlayerToken, CardSide>>
      handleStarterCardSelection() {
    return null;
  }

  public boolean drawStarterCard(PlayerToken playerToken) {
    return false;
  }

  public boolean selectStarterCardSide(PlayerToken playerToken, CardSide cardSide) {
    return false;
  }


  // ObjectiveCardSelectionState methods

  public Map<PlayerToken, ObjectiveCard> handleObjectiveCardSelection() {
    return null;
  }

  public boolean drawObjectiveCards(PlayerToken playerToken) {
    return false;
  }

  public boolean selectObjectiveCard(PlayerToken playerToken, int choice) {
    return false;
  }


  // InitializationState methods

  public boolean handleInitialization(
      Map<String, PlayerToken> idToToken,
      Map<PlayerToken, StarterCard> tokenToStarterCard,
      Map<PlayerToken, CardSide> tokenToCardSide,
      Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard) {
    return false;
  }


  // PostGameState methods

  public boolean postGame() {
    return false;
  }

}
