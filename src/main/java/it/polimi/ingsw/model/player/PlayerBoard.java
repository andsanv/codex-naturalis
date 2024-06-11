package it.polimi.ingsw.model.player;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the area of the board personal to a single player.
 *
 * @see Player
 */
public class PlayerBoard extends Observable {
  /** This attribute holds the starter card's coordinates in the player's board. */
  private static final Coords STARTER_CARD_COORDINATES = new Coords(0, 0);

  /** This attribute represents the player's board */
  private Map<Coords, PlayableCard> board;

  /** This map represents the number of items available on the board. */
  private Map<Elements, Integer> playerItems;

  /**
   * @param starterCard starter card of the player's board
   * @param starterCardSide side of the starter card to play
   */
  public PlayerBoard(StarterCard starterCard, CardSide starterCardSide) {
    this.playerItems =
        new HashMap<Elements, Integer>() {
          {
            put(Resources.PLANT, 0);
            put(Resources.ANIMAL, 0);
            put(Resources.FUNGI, 0);
            put(Resources.INSECT, 0);
            put(Items.INKWELL, 0);
            put(Items.QUILL, 0);
            put(Items.MANUSCRIPT, 0);
          }
        };

    starterCard.playSide(starterCardSide);

    this.board =
        new HashMap<Coords, PlayableCard>() {
          {
            put(STARTER_CARD_COORDINATES, starterCard);
          }
        };

    if (starterCard.getPlayedSide() == CardSide.BACK)
      for (Resources resource : starterCard.centralResources)
        playerItems.put(resource, playerItems.get(resource) + 1);

    for (Corner corner : starterCard.getActiveCorners().values())
      if (corner.getType() == CornerTypes.VISIBLE && corner.getItem().isPresent()) {
        Elements e = corner.getItem().get();
        playerItems.put(e, playerItems.get(e) + 1);
      }
  }

  /**
   * Constructor by copy.
   *
   * @param other other board object
   */
  PlayerBoard(PlayerBoard other) {
    this.board = other.getBoard();
    this.playerItems = other.getPlayerItems();
  }

  /**
   * @param coords coordinates of card
   * @return A map containing non-null adjacent cards
   */
  public Map<CornerPosition, PlayableCard> adjacentCards(Coords coords) {
    return adjacentCoords(coords).entrySet().stream()
        .filter(entry -> getCard(entry.getValue()) != null)
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> getCard(entry.getValue())));
  }

  /**
   * @param coords coordinates of card
   * @return A map containing non-null adjacent corners
   */
  public Map<CornerPosition, Corner> adjacentCorners(Coords coords) {
    return adjacentCoords(coords).entrySet().stream()
        .filter(entry -> getCard(entry.getValue()) != null)
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry ->
                    getCard(entry.getValue())
                        .getActiveCorners()
                        .get(entry.getKey().getOpposite())));
  }

  /**
   * @param coords coordinates of a point
   * @return A map containing the adjacent slots' coordinates
   */
  public Map<CornerPosition, Coords> adjacentCoords(Coords coords) {
    return new HashMap<CornerPosition, Coords>() {
      {
        put(CornerPosition.TOP_LEFT, new Coords(coords.x - 1, coords.y + 1));
        put(CornerPosition.TOP_RIGHT, new Coords(coords.x + 1, coords.y + 1));
        put(CornerPosition.BOTTOM_RIGHT, new Coords(coords.x + 1, coords.y - 1));
        put(CornerPosition.BOTTOM_LEFT, new Coords(coords.x - 1, coords.y - 1));
      }
    };
  }

  /**
   * Used to get all available slots for card placement
   *
   * @return list of available coords
   */
  public List<Coords> availableCoords() {
    return board.keySet().stream()
            .flatMap(x -> adjacentCoords(x).values().stream())
            .filter(x -> !getBoard().containsKey(x))
            .filter(x -> adjacentCorners(x).values().stream().allMatch(Corner::canPlaceCardAbove))
            .collect(Collectors.toList());
  }

  /**
   * @param coords coordinates to check
   * @return True if a card can be placed, false otherwise
   */
  public boolean canPlaceCardAt(Coords coords, PlayableCard card, CardSide side) {
    return getCard(coords) == null
        && !adjacentCards(coords).isEmpty()
        && card.enoughResources(playerItems, side)
        && adjacentCorners(coords).values().stream().allMatch(Corner::canPlaceCardAbove);
  }

  /**
   * Returns the card at the given coordinates. Coords must not be null.
   *
   * @param coords coordinates of the card
   * @return card in given coordinates
   */
  public PlayableCard getCard(Coords coords) {
    return board.get(coords);
  }

  /**
   * Sets the given card at the given position. The params must not be null.
   *
   * @param coords coordinates where to place the card
   * @param card card being placed
   */
  public void setCard(Coords coords, PlayableCard card) {
    board.put(coords, card);
  }

  /**
   * @return returns the player's visible items map
   */
  public Map<Elements, Integer> getPlayerItems() {
    return playerItems;
  }

  /**
   * Updates playerItems after placing the card at coords. Must be called after successfully placing
   * a card.
   *
   * @param coords coordinates of the placed card
   */
  public void updatePlayerItems(Coords coords) {
    // Remove covered items
    adjacentCorners(coords).values().stream()
        .map(Corner::getItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(
            item -> {
              playerItems.put(item, playerItems.get(item) - 1);
            });

    // Add card type resource
    PlayableCard placedCard = this.getCard(coords);
    if (placedCard.getPlayedSide() == CardSide.BACK) {
      Resources cardResource = placedCard.getType().get();
      playerItems.put(cardResource, playerItems.get(cardResource) + 1);
    }

    // Add corner items
    placedCard.getActiveCorners().values().stream()
        .map(Corner::getItem)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(
            item -> {
              playerItems.put(item, playerItems.get(item) + 1);
            });
  }

  /**
   * @return returns the board
   */
  public Map<Coords, PlayableCard> getBoard() {
    return board;
  }
}
