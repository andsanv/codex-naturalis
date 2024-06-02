package it.polimi.ingsw.distributed.commands.game;

import it.polimi.ingsw.controller.GameFlowManager;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;

public class PlayCardCommand extends GameCommand {
  public final PlayerToken playerToken;
  public final Coords coords;
  public final PlayableCard card;
  public final CardSide cardSide;

  public PlayCardCommand(
      PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
    this.playerToken = playerToken;
    this.coords = coords;
    this.card = card;
    this.cardSide = cardSide;
  }

  @Override
  public boolean execute(GameFlowManager gameFlowManager) {
    return gameFlowManager.getCurrentState().playCard(playerToken, coords, card, cardSide);
  }
}
