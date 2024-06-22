package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.SlimGameModel;

/**
 * Event thrown after initializing the model.
 * It contains information on the initial decks, visible cards lists and player hands.
 *
 * @see GameEvent
 */
public class EndedInitializationPhaseEvent extends GameEvent {
    public SlimGameModel slimGameModel;

    /**
     * @param slimGameModel represents the initial collections
     */
    public EndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        this.slimGameModel = slimGameModel;
    }

    @Override
    public void execute(GameEventHandler gameEventHandler) {
        gameEventHandler.handleEndedInitializationPhaseEvent(slimGameModel);
    }
}
