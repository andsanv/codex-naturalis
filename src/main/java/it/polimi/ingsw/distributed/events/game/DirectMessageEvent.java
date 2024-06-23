package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** This event is used to notify that a direct message has been sent. */
public final class DirectMessageEvent extends GameEvent {

  /** The token of the sending player. */
  private final PlayerToken senderToken;

  /** The token of the receiving player. */
  private final PlayerToken receiverToken;

  /** The message sent */
  private final String message;

  /**
   * This constructor creates the event starting from the sender token, the
   * receiver token and the message.
   * 
   * @param senderToken   the token of the sender of the message
   * @param receiverToken the token of the receiver of the message
   * @param message       the message sent
   */
  public DirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
    this.senderToken = senderToken;
    this.receiverToken = receiverToken;
    this.message = message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleDirectMessageEvent(senderToken, receiverToken, message);
  }
}
