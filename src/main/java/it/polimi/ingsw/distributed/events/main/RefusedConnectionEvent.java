package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;

public class RefusedConnectionEvent extends MainEvent {

    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleRefusedReconnection();
    }
    
}
