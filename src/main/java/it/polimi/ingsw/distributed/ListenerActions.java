package it.polimi.ingsw.distributed;

import it.polimi.ingsw.distributed.events.Event;

public interface ListenerActions {
    public Event getLastEvent();
}
