package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

/** This event is used to notify about the end of token phase. */
public class EndedTokenPhaseEvent extends GameEvent {

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedTokenPhaseEvent();
    }
}
