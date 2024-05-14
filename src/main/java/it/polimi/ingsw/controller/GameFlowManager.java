package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.states.*;
import it.polimi.ingsw.distributed.commands.GameCommand;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a single game
 * It contains the in-game connections to the clients, the state machine (and the relative states) and the model
 * Implemented as a runnable in order to be able to run more games on a single server
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
     * Map that keeps track of active connections (not AFK players)
     */
    private Map<User, Boolean> isConnected;

    /**
     * States of the state machine
     */
    public GameState setupState;
    public GameState playCardState;
    public GameState drawCardState;
    public GameState postGameState;

    private GameState currentState;

    /**
     * List containing players' ids
     */
    public List<String> playersIds;

    /**
     * Map from players' ids to their token
     */
    public Map<String, PlayerToken> IdToToken;

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
     * Boolean used by the timer to tell if a player has already made a move or not
     */
    private Boolean timeLimitReached = false;

    private final Queue<GameCommand> commands;

    /**
     * GameFlowManager constructor
     *
     * @param lobby The lobby from which the game was started
     */
    public GameFlowManager(Lobby lobby) {
        this.lobby = lobby;
        isConnected = lobby.getUsers().stream()
                .collect(Collectors.toMap(Function.identity(), u -> false));

        this.playersIds = lobby.getUsers().stream().map(user -> user.name).collect(Collectors.toList());
        this.IdToToken = new HashMap<>();
        this.gameModelUpdater = new GameModelUpdater(new GameModel());

        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.setupState = new SetupState(this);
        this.postGameState = new PostGameState(this);

        this.commands = new LinkedBlockingQueue<>();
        this.currentState = this.setupState;
    }

    /**
     * Override of the Runnable::run method. From this method, the setup phase, the in-game phase and the post game phase are managed
     */
    @Override
    public void run() {
        // pre-game phase
        currentState.setup();

        // in-game phase
        while(!currentState.equals(postGameState)) {
            try {
                (new Thread(this::handleTurn)).join();
            } catch (InterruptedException e) {}
        }

        // post-game phase
        currentState.postGame();
    }

    /**
     * Synchronized, allows to handle a players' turn. A timer is started as soon as player's turn starts
     * If the timer expires before the player makes the move, the turn is skipped
     * Otherwise, the Boolean flag "lastMovePlayed" is set to true by the method called and the timer stops
     * This situation is all managed in the run() method
     *
     * @return The timer thread
     */
    private void handleTurn() {
        Timer timer = new Timer();
        GameCommand command = null;
        timeLimitReached = false;

        TimerTask timeElapsedTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (timeLimitReached) {
                    timeLimitReached = true;
                }
            }
        };

        timer.schedule(timeElapsedTask, timeLimit);

        while(true) {
            if(commands.isEmpty())
                try {
                    commands.wait();    // waits until a command is added to the queue
                } catch (InterruptedException e) {}

            synchronized (timeLimitReached) {
                if (!timeLimitReached) {
                    commands.poll();

                    if (command.execute(this)) {
                        timer.cancel();
                        switchTurn();

                        return;
                    }
                }
                else break;
            }
        }

        if(currentState.equals(drawCardState)) drawRandomCard(getTurn());

        switchTurn();
    }

    public void setState(GameState nextState) {
        currentState = nextState;
    }

    public void addCommand(GameCommand command) {
        synchronized (commands) {   // to make the two lines atomic
            commands.offer(command);
            commands.notifyAll();
        }
    }

    public void drawRandomCard(String playerId) {
        Random rand = new Random();

        switch (rand.nextInt(4)) {
            case 0:
                drawResourceDeckCard(playerId);
                break;
            case 1:
                drawGoldDeckCard(playerId);
                break;
            case 2:
                drawVisibleResourceCard(playerId, rand.nextInt(2));
                break;
            case 3:
                drawVisibleGoldCard(playerId, rand.nextInt(2));
                break;
        }
    }

    /**
     * Allows a player (String) to make a move, which can succeed only if the state machina is in the PlayCardState
     *
     * @param playerId Player ID of the player playing the card
     * @param coords Coordinates on which to place the card
     * @param card Card to place
     * @param cardSide Side to play
     * @return A boolean that depends on whether the operation was successful or not
     */
    public boolean playCard(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        return playerId.equals(getTurn()) && currentState.playCard(IdToToken.get(playerId), coords, card, cardSide);
    }

    /**
     * Allows a player to draw from the ResourceCards deck
     *
     * @param playerId Player ID of the player drawing the card
     * @return A boolean that depends on whether the operation was successful or not
     */
    public boolean drawResourceDeckCard(String playerId) {
        return playerId.equals(getTurn()) && currentState.drawResourceDeckCard(IdToToken.get(playerId));
    }

    /**
     * Allows a player to draw from the GoldCards deck
     *
     * @param playerId Player ID of the player drawing the card
     * @return A boolean that depends on whether the operation was successful or not
     */
    public boolean drawGoldDeckCard(String playerId) {
        return playerId.equals(getTurn()) && currentState.drawGoldDeckCard(IdToToken.get(playerId)));
    }

    /**
     * Allows a player to draw from the list of visible ResourceCards
     *
     * @param playerId Player ID of the player drawing the card
     * @param choice The offset of the card chosen in the list
     * @return A boolean that depends on whether the operation was successful or not
     */
    public boolean drawVisibleResourceCard(String playerId, int choice) {
        return playerId.equals(getTurn()) && currentState.drawVisibleResourceCard(IdToToken.get(playerId), choice))
    }

    /**
     * Allows a player to draw from the list of visible GoldCards
     *
     * @param playerId Player ID of the player drawing the card
     * @param choice The offset of the card chosen in the list
     * @return A boolean that depends on whether the operation was successful or not
     */
    public boolean drawVisibleGoldCard(String playerId, int choice) {
        return playerId.equals(getTurn()) && currentState.drawVisibleGoldCard(IdToToken.get(playerId), choice);
    }

    /**
     * @return The ID of the player whose turn it is
     */
    public String getTurn() {
        return playersIds.get(turn % playersIds.size());
    }

    /**
     * Manages the turns, the rounds and checks whether the next turn will be the last
     */
    public void switchTurn() {
        if(turn % playersIds.size() == playersIds.size() - 1) {
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

    public void setCurrentState(GameState state) {
        currentState = state;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getTimeLimit() {
        return this.timeLimit;
    }
}
