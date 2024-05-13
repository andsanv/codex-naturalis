package it.polimi.ingsw.controller.observer;

import it.polimi.ingsw.distributed.events.GameEvent;

/**
 * Generates GameEvents and sends them to observers.
 * To use this interface an array or collection to keep track of current
 * observers must be used.
 */
public interface Observable {
    /**
     * Used to register an observer
     * 
     * @param observer the observer to send updates to
     */
    public void addObserver(Observer observer);

    /**
     * Must be called to notify an update to all observers.
     * 
     * @param the event to notify
     */
    public void notify(GameEvent event);
}