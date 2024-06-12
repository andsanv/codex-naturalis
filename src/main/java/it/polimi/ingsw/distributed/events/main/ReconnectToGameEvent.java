package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.model.SlimGameModel;

public class ReconnectToGameEvent extends MainEvent {
    // public final SlimModel;

    public final SlimGameModel slimModel;

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReconnetionToGame(/* slim model */);
    }

    public ReconnectToGameEvent(SlimGameModel slimModel) {
        this.slimModel = slimModel;
    }
    
}
