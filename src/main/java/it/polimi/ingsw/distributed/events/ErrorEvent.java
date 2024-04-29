package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.client.ClientCache;

public class ErrorEvent extends Event {
    String error;

    @Override
    public void execute(ClientCache clientCache) {
        clientCache.addError(error);
    }
}
