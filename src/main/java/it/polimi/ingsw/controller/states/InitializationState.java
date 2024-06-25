package it.polimi.ingsw.controller.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.controller.GameModelUpdater;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.events.game.EndedInitializationPhaseEvent;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * State through which the flow manager initializes the model
 */
public class InitializationState extends GameState {
  private final List<Observer> observers;
  private final AtomicInteger lastEventId;
  private final Decks decks;

  public InitializationState(GameFlowManager gameFlowManager, Decks decks, List<Observer> observers, AtomicInteger lastEventId) {
    super(gameFlowManager);

    this.decks = decks;
    this.observers = observers;
    this.lastEventId = lastEventId;
  }

    /**
     * Handles the initialization phase
     *
     * @param userInfoToToken maps players to their tokens
     * @param tokenToStarterCard maps players tokens to their starter card
     * @param tokenToCardSide maps players to their chosen starter card side
     * @param tokenToObjectiveCard maps players to their objective card
     * @return true
     */
  @Override
  public boolean handleInitialization(
      Map<UserInfo, PlayerToken> userInfoToToken,
      Map<PlayerToken, StarterCard> tokenToStarterCard,
      Map<PlayerToken, CardSide> tokenToCardSide,
      Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard
  ) {
    // set up model
    List<ObjectiveCard> commonObjectives = new ArrayList<>(Arrays.asList(decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow(), decks.objectiveCardsDeck.anonymousDraw().first.orElseThrow()));
    List<PlayerToken> playerTokens = new ArrayList<>(userInfoToToken.values());

    GameModelUpdater gameModelUpdater = new GameModelUpdater(
            new GameModel(decks, playerTokens, tokenToStarterCard, tokenToCardSide, tokenToObjectiveCard, commonObjectives, observers, lastEventId)
    );

    // set up controller
    userInfoToToken.entrySet().forEach(x -> gameFlowManager.userInfoToToken.put(x.getKey(), x.getValue()));
    gameFlowManager.setGameModelUpdater(gameModelUpdater);
    gameFlowManager.initializeGameStates();

    // start the game
    gameFlowManager.setState(gameFlowManager.playCardState);
    gameFlowManager.notify(new EndedInitializationPhaseEvent(gameModelUpdater.getSlimGameModel()));
    return true;
  }
}
