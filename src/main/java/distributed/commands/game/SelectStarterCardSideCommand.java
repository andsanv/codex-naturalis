package distributed.commands.game;

import controller.GameFlowManager;
import model.card.CardSide;
import model.player.PlayerToken;

/** Thi command is used to select the starter card side placement. */
public class SelectStarterCardSideCommand extends GameCommand {

  /** Token of the player choosing the side of the starter card. */
  private final PlayerToken playerToken;

  /** The chosen side */
  private final CardSide cardSide;

  /**
   * This constructor creates the command starting from the player token and the selection.
   * 
   * @param playerToken the token of the player choosing.
   * @param cardSide the chosen card side.
   */
  public SelectStarterCardSideCommand(PlayerToken playerToken, CardSide cardSide) {
    this.playerToken = playerToken;
    this.cardSide = cardSide;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.currentState.selectStarterCardSide(playerToken, cardSide);
  }
}
