package distributed.events.game;

import model.SlimGameModel;
import view.interfaces.GameEventHandler;

/**
 * This event is sent after initializing the model.
 * It contains information on the initial decks, visible cards lists and player
 * hands.
 */
public class EndedInitializationPhaseEvent extends GameEvent {
    public SlimGameModel slimGameModel;

    /**
     * This constructor creates the event starting from the slim game model.
     * 
     * @param slimGameModel represents the initial collections.
     */
    public EndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        this.slimGameModel = slimGameModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(GameEventHandler gameEventHandler) {
        gameEventHandler.handleEndedInitializationPhaseEvent(slimGameModel);
    }
}
