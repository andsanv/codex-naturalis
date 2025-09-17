package controller.observer;

import distributed.events.game.GameEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents the observables in the Observer designed pattern.
 * It is mainly used to send from the model game events to the observers, which
 * are the connected players.
 * This class will be extend by classes of the model.
 */
public abstract class Observable {

  /** List of observers to update */
  private final List<Observer> observers;

  /** Last event id */
  private final AtomicInteger lastEventId;

  /**
   * The constructor requires the list of observers and the last event id
   * 
   * @param observers   the list of observers
   * @param lastEventId the last event id
   */
  public Observable(List<Observer> observers, AtomicInteger lastEventId) {
    this.observers = observers;
    this.lastEventId = lastEventId;
  }

  /**
   * Called to notify the list of observers
   *
   * @param event the event to notify
   */
  public void notify(GameEvent event) {
    event.setId(lastEventId.getAndIncrement());
    synchronized (observers) {
      observers.forEach(observer -> observer.update(event));
    }
  }
}
