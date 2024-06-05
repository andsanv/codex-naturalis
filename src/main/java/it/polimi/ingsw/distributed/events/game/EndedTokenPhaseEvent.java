package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

public class EndedTokenPhaseEvent extends GameEvent {
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedTokenPhaseEvent();
    }
}
