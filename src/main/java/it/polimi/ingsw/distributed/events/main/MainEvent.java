package it.polimi.ingsw.distributed.events.main;

import it.polimi.ingsw.distributed.MainEventHandler;
import it.polimi.ingsw.distributed.events.Event;

public abstract class MainEvent extends Event {

  public abstract void execute(MainEventHandler mainEventHandler);
}
