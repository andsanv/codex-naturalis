package distributed.events.game;

import controller.usermanagement.UserInfo;
import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

/** This event is used to notify that a token has been assigned to a player. */
public final class TokenAssignmentEvent extends GameEvent {

  /** The user info of the player. */
  private final UserInfo player;

  /** The token assigned to the player. */
  private final PlayerToken assignedToken;

  /**
   * This constructor creates the event starting from the player and the token.
   * 
   * @param player the player who gets the token.
   * @param assignedToken the given token.
   */
  public TokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
    this.player = player;
    this.assignedToken = assignedToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handleTokenAssignmentEvent(player, assignedToken);
  }
}
