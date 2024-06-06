package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;

import java.util.List;

public class GameResultsEvent extends GameEvent {
    private final List<Pair<PlayerToken, Integer>> gameResults;

    public GameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        this.gameResults = gameResults;
    }

    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleGameResultsEvent(gameResults);
    }
}
