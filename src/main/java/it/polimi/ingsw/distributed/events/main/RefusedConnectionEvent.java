package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.view.interfaces.MainEventHandler;

/** This event is used to notify a client that it request wìììhas been declined. */
public class RefusedConnectionEvent extends MainEvent {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(MainEventHandler mainEventHandler) {
        mainEventHandler.handleRefusedReconnection();
    }
}
