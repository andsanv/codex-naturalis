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

    public GameState currentState;

    public List<String> playersIds;
    public Map<String, PlayerToken> IdToToken;

    public Integer turn = 0;
    public Integer round = 0;
    public Boolean isLastRound = false;

    private long timeLimit = 30;

    public GameFlowManager(Lobby lobby) {
        this.lobby = lobby;
        isConnected = lobby.getUsers().stream()
                .collect(Collectors.toMap(Function.identity(), u -> false));

        this.playCardState = new PlayCardState(this);
        this.drawCardState = new DrawCardState(this);
        this.setupState = new SetupState(this);
        this.postGameState = new PostGameState(this);

        this.IdToToken = new HashMap<String, PlayerToken>();
        this.gameModelUpdater = new GameModelUpdater(new GameModel());

        this.currentState = this.setupState;
        currentState.setup();
    }

    @Override
    public void run() {
        Boolean movePlayed = false;

        // Date start = new Date();
        // Thread timerThread = new Thread(() -> {
        //     try {
        //         while(true)
        //             synchronized(movePlayed) {
        //                 if(movePlayed)
        //                     return;
        //             }

                // if((new Date()).getTime() - start.getTime() > timeLimit) {
                //     if(currentState.equals(drawCardState)) {
                //         Random rand = new Random();

                //         switch(rand.nextInt(4)) {
                //             case 0:
                //                 drawResourceDeckCard(getTurn());
                //                 break;
                //             case 1:
                //                 drawGoldDeckCard(getTurn());
                //                 break;
                //             case 2:
                //                 drawVisibleResourceCard(getTurn(), rand.nextInt(2));
                //                 break;
                //             case 3:
                //                 drawVisibleGoldCard(getTurn(), rand.nextInt(2));
                //                 break;
                //         }
                //     }

                //     increaseTurn();
            //     }
            // } catch (InterruptedException e) {
            //     Thread.currentThread().interrupt();
            // }
        // });


    }

    public void setState(GameState nextState) {
        currentState = nextState;
    }

    public boolean playCard(String playerId, Coords coords, PlayableCard card, CardSide cardSide) {
        // called = true;

        return playerId.equals(getTurn()) && currentState.playCard(IdToToken.get(playerId), coords, card, cardSide);
    }

    public boolean drawResourceDeckCard(String playerId) {
        return playerId.equals(getTurn()) && currentState.drawResourceDeckCard(IdToToken.get(playerId));
    }

    public boolean drawGoldDeckCard(String playerId) {
        return playerId.equals(getTurn()) && currentState.drawGoldDeckCard(IdToToken.get(playerId));
    }

    public boolean drawVisibleResourceCard(String playerId, int choice) {
        return playerId.equals(getTurn()) && currentState.drawVisibleResourceCard(IdToToken.get(playerId), choice);
    }

    public boolean drawVisibleGoldCard(String playerId, int choice) {
        return playerId.equals(getTurn()) && currentState.drawVisibleGoldCard(IdToToken.get(playerId), choice);
    }

    private String getTurn() {
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
}

/**
 * The Game class represents a single game.
 * It contains the in-game connections to the clients, the controller and the
 * model.
 */

