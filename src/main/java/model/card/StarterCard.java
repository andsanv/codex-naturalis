package model.card;

import model.common.Resources;
import model.corner.Corner;
import model.corner.CornerPosition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A starter card is the first card placed on each player's board.
 * At the start of the match, each player randomly draws one starter card from the deck.
 *
 * @see Card
 * @see PlayableCard
 */
public class StarterCard extends PlayableCard {
  /**
   * Set to hold the resources of the front of the card.
   */
  private final Set<Resources> centralResources;

  /**
   * @param centralResources resources of the front of the card
   * @param frontCorners corners of the front of the card
   * @param backCorners corners of the back of the card
   */
  public StarterCard(
      int id,
      Set<Resources> centralResources,
      Map<CornerPosition, Corner> frontCorners,
      Map<CornerPosition, Corner> backCorners) {
    super(id, frontCorners, backCorners, null, PointsType.ZERO);

    this.centralResources = centralResources;
  }

  /**
   * CentralResources' getter.
   * Private final and getter with copy (instead of public) to make the set constant.
   *
   * @return (a copy of) card's central resources
   */
  public Set<Resources> getCentralResources() {
    return new HashSet<>(centralResources);
  }
}
