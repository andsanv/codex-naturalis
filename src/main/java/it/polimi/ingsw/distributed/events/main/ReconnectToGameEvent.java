package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.model.SlimGameModel;

/**
 * This event is used to notify that the user is reconnecting to a game.
 * It constains the slim model of the game as its necessary to recreate the game state.
 */
public class ReconnectToGameEvent extends MainEvent {
    
    /** The slim game model */
    public final SlimGameModel slimModel;

    /**
     * This constructor creates the event starting from the slim model of the game.
     * 
     * @param slimModel the slim model of the game.
     */
    public ReconnectToGameEvent(SlimGameModel slimModel) {
        this.slimModel = slimModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReconnetionToGame(/* slim model */);
    }
}
