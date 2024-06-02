package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.states.*;
import it.polimi.ingsw.distributed.commands.game.GameCommand;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a single game
 * It contains the in-game connections to the clients, the state machine (and the relative states) and the model
 * Implemented as a runnable in order to be able to run more games on a single server
 * Uses the command pattern to keep track of every player's moves
 */


public class GameFlowManager implements Runnable {
    /**
     * Part of the controller that updates the model
     */
    public GameModelUpdater gameModelUpdater;

    /**
     * Represents the lobby, or the "room" containing the players (more games can be started sequentially from a single lobby)
     */
    private Lobby lobby;

    /**
     * Map that keeps track of active connections (or not-AFK players)
     */
    private Map<User, Boolean> isConnected;

    /**
     * States of the state machine
     */
    public GameState tokenSelectionState;
    public GameState starterCardSelectionState;
    public GameState objectiveCardSelectionState;
    public GameState initializationState;
    public GameState playCardState;
    public GameState drawCardState;
    public GameState postGameState;

    private GameState currentState;

    /**
     * List containing players' ids
     */
    public List<User> users;

    /**
     * Map from players' ids to their token
     */
    public final Map<String, PlayerToken> idToToken;
    public final List<PlayerToken> playerTokens;

    /**
     * Variables to keep track of the turn / round of the game
     */
    public Integer turn = 0;
    public Integer round = 0;
    public Boolean isLastRound = false;

    /**
     * Time limit for a player to make his move (in seconds)
     */
    private long timeLimit = 60;

    /**
     * Boolean used by the timer to tell whether time limit has been reached or not
     */
    private final AtomicBoolean timeLimitReached = new AtomicBoolean(false);

    /**
     * Queue of commands received by the gameFlowManager. Every move made by a player is identified by a command.
     * Initialized as a blocking queue.
     */
    public final Queue<GameCommand> commands;

    public final List<Observer> observers;

    /**
     * GameFlowManager constructor
     *
     * @param lobby The lobby from which the game was started
     */
    public GameFlowManager(Lobby lobby, List<Observer> observers) {
        this.lobby = lobby;
        this.users = lobby.getUsers();
        users.forEach(user -> this.isConnected.put(user, false));

        this.idToToken = new HashMap<>();
        this.playerTokens = new ArrayList<>();

        this.observers = observers;

        this.gameModelUpdater = new GameModelUpdater(new GameModel(), this.observers);

        this.tokenSelectionState = new TokenSelectionState(this, users, timeLimit);
        this.starterCardSelectionState = new StarterCardSelectionState(this, playerTokens, timeLimit);
        this.objectiveCardSelectionState = new ObjectiveCardSelectionState(this, playerTokens, timeLimit);
        this.initializationState = new InitializationState(this);
        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.postGameState = new PostGameState(this);

        this.commands = new LinkedBlockingQueue<>();
        this.currentState = this.tokenSelectionState;
    }

    /**
     * Override of the Runnable::run method. From this method, the setup phase, the in-game phase and the post game phase are managed
     */
    @Override
    public void run() {
        // pre-game phase
        Map<String, PlayerToken> idToToken = currentState.handleTokenSelection();            // select token phase
        Pair<Map<PlayerToken, StarterCard>, Map<PlayerToken, CardSide>> tokenToStarterCardAndCardSide = currentState.handleStarterCardSelection();      // select starter card side phase
        Map<PlayerToken, StarterCard> tokenToStarterCard = tokenToStarterCardAndCardSide.first;
        Map<PlayerToken, CardSide> tokenToCardSide = tokenToStarterCardAndCardSide.second;
        Map<PlayerToken, ObjectiveCard> tokenToObjectiveCard = currentState.handleObjectiveCardSelection();    // select objective card phase

        currentState.handleInitialization(idToToken, tokenToStarterCard, tokenToCardSide, tokenToObjectiveCard);

        // in-game phase
        while(!currentState.equals(postGameState)) {
            try {
                Thread turnHandlerThread = new Thread(this::handleTurn);
                turnHandlerThread.start();
                turnHandlerThread.join();
            } catch (InterruptedException e) {}
        }

        // post-game phase
        currentState.postGame();
    }

    /**
     * Synchronized, allows to handle a players' turn. A timer is started as soon as player's turn starts
     * If the timer expires before the player makes the move, the turn is skipped
     * This situation is all managed in the run() method
     */
    private void handleTurn() {
        Timer timer = new Timer();
        timeLimitReached.set(false);

        TimerTask timeElapsedTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (timeLimitReached) {
                    timeLimitReached.set(true);
                }
            }
        };

        timer.schedule(timeElapsedTask, timeLimit * 1000);

        while(true) {
            if (!timeLimitReached.get())
                synchronized (commands) {
                    if (!commands.isEmpty() && commands.poll().execute(this)) {
                        timer.cancel();

                        if(currentState.equals(playCardState)) currentState = drawCardState;
                        else switchTurn();

                        return;
                    }
                    else {
                        // view.displayError("error");
                    }
                }
            else break;
        }

        if(currentState.equals(drawCardState)) drawRandomCard(getTurn());

        switchTurn();
    }

    public void setState(GameState nextState) {
        currentState = nextState;
    }

    /**
     * Adds a command to the commands queue.
     * @param command The command representing the single player's move
     */
    public void addCommand(GameCommand command) {
        synchronized (commands) {   // to make the two lines atomic
            commands.add(command);
            commands.notifyAll();
        }
    }

    /**
     * Used to fill a player's hand when he exceeds his time limit
     *
     * @param PlayerToken Token that represents the player
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
     * @return The ID of the player whose turn it is
     */
    public PlayerToken getTurn() {
        return idToToken.get(users.get(turn % users.size()).name);
    }

    /**
     * Manages the turns, the rounds and checks whether the next turn will be the last
     */
    public void switchTurn() {
        if(turn % users.size() == users.size() - 1) {
            if(isLastRound) {
                setState(postGameState);
                return;
            }
            else {
                if(gameModelUpdater.limitPointsReached() || gameModelUpdater.someDecksEmpty())
                    isLastRound = true;
                round += 1;
            }
        }

        turn += 1;
        setState(playCardState);
    }

    public Map<User, Boolean> getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(User user, Boolean isConnected) {
        getIsConnected().put(user, isConnected);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getTimeLimit() {
        return this.timeLimit;
    }
}
