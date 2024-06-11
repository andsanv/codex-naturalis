package it.polimi.ingsw.model.card;

import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import java.util.HashMap;
import java.util.Map;

/**
 * A gold card is a playable card that requires resources to be placed on the board.
 * All gold cards give points when played on the front side.
 *
 * @see Card
 * @see PlayableCard
 */
public class GoldCard extends PlayableCard {
  /**
   * Map that represents the number of resources of each type required to play the card.
   *
   * @see Resources
   */
  private final Map<Resources, Integer> requiredResources;

  /**
   * @param type seed of the card
   * @param points points given when playing the front of the card
   * @param requiredResources resources needed to play the card
   * @param frontCorners corners of the front of the card
   * @param backCorners corners of the back of the card
   */
  public GoldCard(
      int id,
      Map<CornerPosition, Corner> frontCorners,
      Map<CornerPosition, Corner> backCorners,
      Resources type,
      PointsType points,
      Map<Resources, Integer> requiredResources) {
    super(id, frontCorners, backCorners, type, points);
    this.requiredResources = requiredResources;
  }

  /**
   * Method to verify if the player has enough resources to play the card, on the specified side.
   *
   * @param playerResources resources owned by the player
   * @param side card side played
   * @return true if player has enough resources, false otherwise
   */
  @Override
  public boolean enoughResources(Map<Elements, Integer> playerResources, CardSide side) {
    return side == CardSide.BACK
        || requiredResources.entrySet().stream()
            .allMatch(e -> playerResources.get(e.getKey()) >= e.getValue());
  }

  /**
   * @return (a copy of) resources required to play the card
   */
  public Map<Resources, Integer> getRequiredResources() {
    return new HashMap<>(requiredResources);
  }
}
