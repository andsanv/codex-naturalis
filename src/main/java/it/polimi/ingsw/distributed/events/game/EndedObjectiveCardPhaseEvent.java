package it.polimi.ingsw.distributed.events.game;
import it.polimi.ingsw.distributed.GameEventHandler;

/** This event is sento to notify then end of the objective cards drawing phase. */
public class EndedObjectiveCardPhaseEvent extends GameEvent {

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedTokenPhaseEvent();
    }
}
