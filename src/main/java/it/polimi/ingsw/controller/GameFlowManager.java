package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.controller.states.DrawCardState;
import it.polimi.ingsw.controller.states.GameState;
import it.polimi.ingsw.controller.states.InitializationState;
import it.polimi.ingsw.controller.states.ObjectiveCardSelectionState;
import it.polimi.ingsw.controller.states.PlayCardState;
import it.polimi.ingsw.controller.states.PostGameState;
import it.polimi.ingsw.controller.states.StarterCardSelectionState;
import it.polimi.ingsw.controller.states.TokenSelectionState;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.distributed.events.game.GameEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.deck.Decks;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// CHANGE FROM USER TO USERINFO
//

/**
 * This class allows to manage a single game.
 * It's the middle point between the clients and the game model.
 * Implemented as a runnable in order to be able to run more games on a single server.
 *
 * Uses a state pattern to manage the flow of the game.
 * Uses the command pattern to keep track of every player's moves.
 *
 * @see GameState
 * @see GameCommand
 * @see GameModelUpdater
 */
public class GameFlowManager implements Runnable {
  /**
   * Part of the controller that updates the model.
   * Middle point between GameFlowManager and GameState.
   */
  public GameModelUpdater gameModelUpdater;

  /**
   * Represents the lobby, or the "room" containing the players (more games can be started sequentially from a single lobby)
   */
  private final Lobby lobby;

  /**
   * Map that keeps track of active connections (non AFK players)
   */
  private final Map<UserInfo, Boolean> isConnected;

  /**
   * List containing all in game users.
   */
  public List<User> users;

  /**
   * List of observers (the clients connected), which will be notified for every event thrown.
   */
  public final List<Observer> observers;

  /**
   * Queue of commands sent by the players.
   * Every move made by a player is identified by a command.
   * Implemented as a blocking queue.
   */
  public final Queue<GameCommand> commands;

  /**
   * Map from UserInfo of players to their token.
   */
  public final Map<UserInfo, PlayerToken> userInfoToToken;

  /**
   * List of tokens playing.
   */
  public final List<PlayerToken> playerTokens;

  /**
   * Integer to keeps track of the turn number.
   */
  public Integer turn = 0;

  /**
   * Integer to keeps track of the round number.
   */
  public Integer round = 0;

  /**
   * True if someone reached the limit score, else otherwise
   */
  public Boolean isLastRound = false;

  /** Time limit for a player to make his move (in seconds) */
  private final long timeLimit = 60;

  /** Boolean used by the timer to tell whether time limit has been reached or not */
  private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);

  /**
   * States of the state machine.
   */
  public GameState tokenSelectionState;
  public GameState starterCardSelectionState;
  public GameState objectiveCardSelectionState;
  public GameState initializationState;
  public GameState playCardState;
  public GameState drawCardState;
  public GameState postGameState;
  public GameState currentState;


  /**
   * @param lobby lobby from which the game was started
   * @param isConnected map from user to a connection boolean, used to skip turn if client is no more connected
   * @param observers list of observers
   */
  public GameFlowManager(Lobby lobby, Map<UserInfo, Boolean> isConnected, List<Observer> observers) {
    this.lobby = lobby;
    this.isConnected = isConnected;

    this.userInfoToToken = new HashMap<>();
    this.playerTokens = new ArrayList<>();

    this.observers = observers;
    AtomicInteger lastEventId = new AtomicInteger(0);

    this.commands = new LinkedBlockingQueue<>();

    Decks decks = new Decks(observers, lastEventId);
    this.tokenSelectionState = new TokenSelectionState(this, users, timeLimit);
    this.starterCardSelectionState = new StarterCardSelectionState(this, decks, playerTokens, timeLimit);
    this.objectiveCardSelectionState = new ObjectiveCardSelectionState(this, decks, playerTokens, timeLimit);
    this.initializationState = new InitializationState(this, decks, observers, lastEventId);

    this.currentState = this.tokenSelectionState;
  }

  /**
   * Override of the Runnable::run method.
   *
   * As soon as the thread is started, the setup phase begins, where players choose their token, starter card and objective card.
   * Then the in-game phase starts, where players place and draw cards, until someone reaches the limit score.
   * When the game ends, the post-game phase starts.
   */
  @Override
  public void run() {
    // pre-game phase
    Map<String, PlayerToken> idToToken = currentState.handleTokenSelection(playerTokens); // select token phase
    Pair<Map<PlayerToken, StarterCard>, Map<PlayerToken, CardSide>> tokenToStarterCardAndCardSide = currentState.handleStarterCardSelection(); // select starter card side phase
    Map<PlayerToken, StarterCard> tokenToStarterCard = tokenToStarterCardAndCardSide.first;
    Map<PlayerToken, CardSide> tokenToCardSide = tokenToStarterCardAndCardSide.second;
    Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard = currentState.handleObjectiveCardSelection(); // select objective card phase

    currentState.handleInitialization(
        idToToken, tokenToStarterCard, tokenToCardSide, tokenToObjectiveCard
    );

    // in-game phase
    while (!currentState.equals(postGameState)) {
      try {
        Thread turnHandlerThread = new Thread(this::handleTurn);
        turnHandlerThread.start();
        turnHandlerThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // post-game phase
    currentState.postGame();
  }

  /**
   * Synchronized, allows to handle a players' turn. A timer is started as soon as player's turn starts.
   * If the timer expires before the player makes his move, the turn is skipped.
   */
  private void handleTurn() {
    Timer timer = new Timer();
    timeLimitReached.set(false);

    TimerTask timeElapsedTask =
        new TimerTask() {
          @Override
          public void run() {
            synchronized (timeLimitReached) {
              timeLimitReached.set(true);
            }
          }
        };

    timer.schedule(timeElapsedTask, timeLimit * 1000);

    while (true) {
      if (timeLimitReached.get()) {
        // time limit reached event
        break;
      }
      if (!isConnected.get(userInfoToToken.keySet().stream().filter(user -> userInfoToToken.get(user).equals(getTurn())).findAny().orElseThrow())) {
        // user not connected event
        break;
      }
      synchronized (commands) {
        if (!commands.isEmpty() && commands.poll().execute(this)) {
          timer.cancel();

          if (currentState.equals(playCardState)) currentState = drawCardState;
          else switchTurn();

          return;
        }
      }
    }

    if (currentState.equals(drawCardState)) drawRandomCard(getTurn());

    switchTurn();
  }

  /**
   * Allows to change GameFlowManager's current state.
   *
   * @param nextState next state
   */
  public void setState(GameState nextState) {
    currentState = nextState;
  }

  /**
   * Adds a command to the commands queue.
   *
   * @param command the command representing the player's move
   */
  public void addCommand(GameCommand command) {
    synchronized (commands) { // to make the two lines atomic
      commands.add(command);
      commands.notifyAll();
    }
  }

  /**
   * Used to fill a player's hand when he exceeds his time limit.
   *
   * @param playerToken token that represents the player
   */
  public void drawRandomCard(PlayerToken playerToken) {
    Random rand = new Random();

    switch (rand.nextInt(4)) {
      case 0:
        currentState.drawResourceDeckCard(playerToken);
        break;
      case 1:
        currentState.drawGoldDeckCard(playerToken);
        break;
      case 2:
        currentState.drawVisibleResourceCard(playerToken, rand.nextInt(2));
        break;
      case 3:
        currentState.drawVisibleGoldCard(playerToken, rand.nextInt(2));
        break;
    }
  }

  /**
   * @return token of the player whose turn it is.
   */
  public PlayerToken getTurn() {
    return userInfoToToken.get(users.get(turn % users.size()).name);
  }

  /**
   * Manages the turns, the rounds and checks whether the next turn will be the last.
   */
  public void switchTurn() {
    if (turn % users.size() == users.size() - 1) {
      if (isLastRound) {
        setState(postGameState);
        return;
      } else {
        if (gameModelUpdater.limitScoreReached() || gameModelUpdater.anyDeckEmpty())
          isLastRound = true;
        round += 1;
      }
    }

    turn += 1;
    setState(playCardState);
  }

  /**
   * Updates clients on a certain event. Used mainly during "setup" phase
   *
   * @param gameEvent event to send to clients
   */
  public void notify(GameEvent gameEvent) {
    observers.forEach(observer -> {
      try {
        observer.update(gameEvent);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * GameModelUpdater and GameModel are instantiated only after setup phase is finished.
   *
   * @param gameModelUpdater the newly instantiated GameModelUpdater
   */
  public void setGameModelUpdater(GameModelUpdater gameModelUpdater) {
    this.gameModelUpdater = gameModelUpdater;
  }

  /**
   * Side method to set up some game states, that require a GameModelUpdater given as parameter.
   */
  public void initializeGameStates() {
    this.playCardState = new PlayCardState(this);
    this.drawCardState = new DrawCardState(this);
    this.postGameState = new PostGameState(this);
  }
}
