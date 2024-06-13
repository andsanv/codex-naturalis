package it.polimi.ingsw.controller.observer;

import it.polimi.ingsw.distributed.events.game.GameEvent;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates GameEvents and sends them to observers. To use this interface an array or collection to
 * keep track of current observers must be used.
 */
public abstract class Observable {
  private final List<Observer> observers;
  private final AtomicInteger lastEventId;

  public Observable(List<Observer> observers, AtomicInteger lastEventId) {
    this.observers = observers;
    this.lastEventId = lastEventId;
  }

  /**
   * Must be called to notify an update to all observers.
   *
   * @param event the event to notify
   */
  public void notify(GameEvent event) {
    event.setId(lastEventId.getAndIncrement());
    synchronized (observers) {
      observers.forEach(observer -> {
        try {
          observer.update(event);
        } catch (RemoteException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      });
    }
  }
}
