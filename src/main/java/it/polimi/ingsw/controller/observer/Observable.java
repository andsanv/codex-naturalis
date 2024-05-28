package it.polimi.ingsw.controller.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.polimi.ingsw.distributed.events.game.GameEvent;

/**
 * Generates GameEvents and sends them to observers.
 * To use this interface an array or collection to keep track of current
 * observers must be used.
 */
public abstract class Observable {
    private List<Observer> observers;
    private AtomicInteger lastEventId;

    public Observable(AtomicInteger lastEventId) {
        this.observers = new ArrayList<>();
        this.lastEventId = lastEventId;
    }

    /**
     * Used to register an observer
     * 
     * @param observer the observer to send updates to
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Must be called to notify an update to all observers.
     * 
     * @param the event to notify
     */
    public void notify(GameEvent event) {
        event.setId(lastEventId.getAndIncrement());
        observers.forEach(observer -> observer.update(event));
    }
}