package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import java.util.Map;

/**
 * A resource card is a playable card that doesn't require any resources to be placed on the board.
 * Some resource cards can give points when played on the front side.
 *
 * @see Card
 * @see PlayableCard
 */
public class ResourceCard extends PlayableCard {
  /**
   * @param type card's seed
   * @param pointsType points given when playing the front of the card
   * @param frontCorners corners of the front of the card
   * @param backCorners corners of the back of the card
   */
  public ResourceCard(
      int id,
      Resources type,
      PointsType pointsType,
      Map<CornerPosition, Corner> frontCorners,
      Map<CornerPosition, Corner> backCorners) {
    super(id, frontCorners, backCorners, type, pointsType);
  }

}
