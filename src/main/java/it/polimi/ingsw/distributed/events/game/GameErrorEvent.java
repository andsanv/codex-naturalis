package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;

public class GameErrorEvent extends GameEvent {
  private final String message;

  public GameErrorEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleGameError(message);
  }
}
