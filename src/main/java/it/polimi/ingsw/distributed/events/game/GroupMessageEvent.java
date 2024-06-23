package it.polimi.ingsw.distributed.events.game;

import it.polimi.ingsw.distributed.GameEventHandler;
import it.polimi.ingsw.model.player.PlayerToken;

/** Thi event is used to notify that a group message has been sent. */
public final class GroupMessageEvent extends GameEvent {

  /** The token of the player sending the message. */
  private final PlayerToken senderToken;

  /** The message sent. */
  private final String message;

  /**
   * This constructor creates the event starting from the sender token and the
   * message.
   * 
   * @param senderToken the token of the sender of the message.
   * @param message     the message sent.
   */
  public GroupMessageEvent(PlayerToken senderToken, String message) {
    this.senderToken = senderToken;
    this.message = message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleGroupMessageEvent(senderToken, message);
  }
}
