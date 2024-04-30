package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.states.*;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class GameFlowManager implements Runnable {
    public GameModelUpdater gameModelUpdater;

    private Lobby lobby;
    private Map<User, Boolean> isConnected;

    public GameState setupState;
    public GameState playCardState;
    public GameState drawCardState;
    public GameState postGameState;

    private GameState currentState;

    public List<String> playersIds;
    public Map<String, PlayerToken> IdToToken;

    public Integer turn = 0;
    public Integer round = 0;
    public Boolean isLastRound = false;

    private long timeLimit = 30;
    private Boolean lastMovePlayed = false;

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

        this.currentState = this.setupState;
    }

    @Override
    public void run() {
        // pre-game phase
        currentState.setup();

        // in-game phase
        while(!currentState.equals(postGameState)) {
            Thread thread = handleTurn();
            try {
                thread.join();
            }
            catch(InterruptedException e) {}
        }

        // post-game phase
        currentState.postGame();
    }

    private Thread handleTurn() {
        Date start = new Date();
        lastMovePlayed = false;

        Thread thread = new Thread(() -> {
            while(true) {
                synchronized(lastMovePlayed) {
                    if(lastMovePlayed) {
                        return;
                    }
                }

                if((new Date()).getTime() - start.getTime() > timeLimit * 1000) {
                    if(currentState.equals(playCardState)) {
                        increaseTurn();
                    }
                    else {
                        Random rand = new Random();

                        switch(rand.nextInt(4)) {
                            case 0:
                                drawResourceDeckCard(getTurn());
                                break;
                            case 1:
                                drawGoldDeckCard(getTurn());
                                break;
                            case 2:
                                drawVisibleResourceCard(getTurn(), rand.nextInt(2));
                                break;
                            case 3:
                                drawVisibleGoldCard(getTurn(), rand.nextInt(2));
                                break;
                        }
                    }
                    return;
                }
            }
        });

        thread.start();

        return thread;
    }

    public void setState(GameState nextState) {
        currentState = nextState;
    }

    public boolean playCard(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        synchronized (lastMovePlayed) {
            if (playerId.equals(getTurn()) && currentState.playCard(IdToToken.get(playerId), coords, card, cardSide)) {
                lastMovePlayed = true;
                return true;
            }

            return false;
        }
    }

    public boolean drawResourceDeckCard(String playerId) {
        synchronized (lastMovePlayed) {
            if (playerId.equals(getTurn()) && currentState.drawResourceDeckCard(IdToToken.get(playerId))) {
                lastMovePlayed = true;
                return true;
            }
            return false;
        }
    }

    public boolean drawGoldDeckCard(String playerId) {
        synchronized (lastMovePlayed) {
            if (playerId.equals(getTurn()) && currentState.drawGoldDeckCard(IdToToken.get(playerId))) {
                lastMovePlayed = true;
                return true;
            }
            return false;
        }
    }

    public boolean drawVisibleResourceCard(String playerId, int choice) {
        synchronized (lastMovePlayed) {
            if (playerId.equals(getTurn()) && currentState.drawVisibleResourceCard(IdToToken.get(playerId), choice)) {
                lastMovePlayed = true;
                return true;
            }
            return false;
        }
    }

    public boolean drawVisibleGoldCard(String playerId, int choice) {
        synchronized (lastMovePlayed) {
            if (playerId.equals(getTurn()) && currentState.drawVisibleGoldCard(IdToToken.get(playerId), choice)) {
                lastMovePlayed = true;
                return true;
            }
            return false;
        }
    }

    public String getTurn() {
        return playersIds.get(turn % playersIds.size());
    }

    public void increaseTurn() {
        if(turn % playersIds.size() == playersIds.size() - 1) {
            if(isLastRound) {
                // stuff
                setState(postGameState);
                // currentState.handlePostGame();
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

/**
 * The Game class represents a single game.
 * It contains the in-game connections to the clients, the controller and the
 * model.
 */

