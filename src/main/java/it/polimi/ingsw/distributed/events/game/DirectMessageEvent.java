package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Event to signal that a direct message has been sent. */
public final class DirectMessageEvent extends GameEvent {
  private final PlayerToken senderToken;
  private final PlayerToken receiverToken;
  private final String message;

  /**
   * @param senderToken the token of the sender of the message
   * @param receiverToken the token of the receiver of the message
   * @param message the message sent
   */
  public DirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
    this.senderToken = senderToken;
    this.receiverToken = receiverToken;
    this.message = message;
  }

  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDirectMessageEvent(senderToken, receiverToken, message);
  }
}
