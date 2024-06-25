package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

/** This event is used to notify about the end of the starter cards drawing. */
public class EndedStarterCardPhaseEvent extends GameEvent {

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedStarterCardPhaseEvent();
    }
}
