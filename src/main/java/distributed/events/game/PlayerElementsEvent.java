package distributed.events.game;

import model.common.Elements;
import model.player.PlayerToken;
import view.interfaces.GameEventHandler;

import java.util.Map;

/** This event is used to notify about a player's updated elements' map. */
public final class PlayerElementsEvent extends GameEvent {

  /** The player's token */
  public final PlayerToken playerToken;

  /** The updated elements map */
  public final Map<Elements, Integer> resources;

  /**
   * This constructor creates the event starting from the player token and the
   * updated elements map.
   * 
   * @param playerToken the token of the player that gets his elements updated
   * @param resources   the updated elements map
   */
  public PlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
    this.playerToken = playerToken;
    this.resources = resources;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(GameEventHandler gameUpdateHandler) {
    gameUpdateHandler.handlePlayerElementsEvent(playerToken, resources);
  }
}
