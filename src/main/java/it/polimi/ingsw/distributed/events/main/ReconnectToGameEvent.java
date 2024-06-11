package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

public class ReconnectToGameEvent extends MainEvent {
    // public final SlimModel;

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleReconnetionToGame(/* slim model */);
    }

    public ReconnectToGameEvent(/* SlimModel */) {
        // this.slimModel = slimModel;
    }

    
    
}
