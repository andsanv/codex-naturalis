package distributed.events.game;

import view.interfaces.GameEventHandler;

/** This event is used to notify about the end of the starter cards drawing. */
public class EndedStarterCardPhaseEvent extends GameEvent {

    /**
     * {@inheritDoc}
     */
    public void execute(GameEventHandler gameUpdateHandler) {
        gameUpdateHandler.handleEndedStarterCardPhaseEvent();
    }
}
