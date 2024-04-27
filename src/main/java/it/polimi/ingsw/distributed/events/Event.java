package it.polimi.ingsw.distributed.events;

import it.polimi.ingsw.client.lightModel.ClientModel;

public abstract class Event {
    public abstract void update(ClientModel model);
}
