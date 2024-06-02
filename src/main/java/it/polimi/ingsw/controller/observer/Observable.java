package it.polimi.ingsw.controller.observer;

import it.polimi.ingsw.distributed.events.game.GameEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates GameEvents and sends them to observers. To use this interface an array or collection to
 * keep track of current observers must be used.
 */
public abstract class Observable {
  private static List<Observer> observers;
  private static AtomicInteger lastEventId;

  public static void setObservers(List<Observer> observers) {
    Observable.observers = observers;
  }

  public static void setLastEventId(AtomicInteger lastEventId) {
    Observable.lastEventId = lastEventId;
  }

  /**
   * Must be called to notify an update to all observers.
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
