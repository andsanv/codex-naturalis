package distributed.events.game;
import view.interfaces.GameEventHandler;

/** This event is sento to notify then end of the objective cards drawing phase. */
public class EndedObjectiveCardPhaseEvent extends GameEvent {

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedObjectiveCardPhaseEvent();
    }
}
