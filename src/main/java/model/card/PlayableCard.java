package model.card;

import model.common.Elements;
import model.common.Resources;
import model.corner.Corner;
import model.corner.CornerPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A playable card is a card that can be placed on the player's board.
 * The class is abstract, as concrete cards will be ResourceCards and GoldCards.
 *
 * @see Card
 */
public abstract class PlayableCard extends Card {
  /**
   * Maps a CornerPosition to a Corner object, on the front card side.
   */
  private final Map<CornerPosition, Corner> frontCorners;

  /**
   * Maps a CornerPosition to a Corner object, on the back card side.
   */
  private final Map<CornerPosition, Corner> backCorners;

  /**
   * Maps a CornerPosition to a Corner object, on the played card side.
   * Null until the card is played.
   * Can be considered "redundant", implemented for an increased ease of use.
   */
  private final Map<CornerPosition, Corner> activeCorners;

  /**
   * The played card side.
   */
  private CardSide playedSide;

  /**
   * Card's seed.
   * Optional to allow StarterCards to have an empty value.
   */
  public final Optional<Resources> type;

  /**
   * Type of points assigned to the player when placing this card on the front side.
   *
   * @see PointsType
   */
  public final PointsType pointsType;

  /**
   * @param id unique id of the card
   * @param frontCorners corners on the front of the card
   * @param backCorners corners on the back of the card
   * @param type card's seed
   * @param pointsType type of points assignment
   */
  public PlayableCard(
      int id,
      Map<CornerPosition, Corner> frontCorners,
      Map<CornerPosition, Corner> backCorners,
      Resources type,
      PointsType pointsType) {
    super(id);

    this.frontCorners = frontCorners;
    this.backCorners = backCorners;
    this.activeCorners = new HashMap<>();

    this.type = Optional.ofNullable(type);
    this.pointsType = pointsType;

    this.playedSide = null;
  }

  /**
   * Method to verify if the player has enough resources to play the card, on the specified side.
   *
   * @param playerResources resources owned by the player
   * @param side played card side
   * @return true as in default case, a card has zero resources needed
   */
  public boolean enoughResources(Map<Elements, Integer> playerResources, CardSide side) {
    return true;
  }

  /**
   * Allows a user to play a side of the card.
   *
   * @param playedSide card side chosen
   */
  public void playSide(CardSide playedSide) {
    this.playedSide = playedSide;
    activeCorners.clear();
    activeCorners.putAll(playedSide == CardSide.FRONT ? frontCorners : backCorners);
  }

  /**
   * playedSide's getter.
   *
   * @return null if the card has not been played yet, a CardSide otherwise
   */
  public CardSide getPlayedSide() {
    return playedSide;
  }

  /**
   * frontCorners' getter.
   * Private final and getter with copy (instead of public) to make the map constant.
   *
   * @return (a copy of) card's front corners
   */
  public Map<CornerPosition, Corner> getFrontCorners() {
    return new HashMap<>(frontCorners);
  }

  /**
   * backCorners' getter.
   * Private final and getter with copy (instead of public) to make the map constant.
   *
   * @return (a copy of) card's back corners
   */
  public Map<CornerPosition, Corner> getBackCorners() {
    return new HashMap<>(backCorners);
  }

  /**
   * activeCorners' getter.
   *
   * @return card's active corners
   */
  public Map<CornerPosition, Corner> getActiveCorners() {
    return activeCorners;
  }
}
