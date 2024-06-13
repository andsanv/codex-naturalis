package it.polimi.ingsw.model.player;

import it.polimi.ingsw.controller.observer.Observable;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.distributed.events.game.PlayedCardEvent;
import it.polimi.ingsw.distributed.events.game.PlayerElementsEvent;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class represents the area of the whole game table related to a single player.
 *
 * @see Player
 */
public class PlayerBoard extends Observable {
  /**
   * Starter card's coordinates in the player's board.
   */
  private static final Coords STARTER_CARD_COORDINATES = new Coords(0, 0);

  /**
   * Player's board.
   * Implemented as a map from coordinates to played card.
   */
  public final Map<Coords, PlayableCard> board;

  /**
   * Number of elements available on the board.
   */
  public final Map<Elements, Integer> playerElements;

  /**
   * @param starterCard starter card of the player's board
   * @param starterCardSide side of the starter card to play
   * @param observers list of observers
   * @param lastEventId integer used to uniquely identify events
   */
  public PlayerBoard(StarterCard starterCard, CardSide starterCardSide, List<Observer> observers, AtomicInteger lastEventId) {
    super(observers, lastEventId);

    this.playerElements =
        new HashMap<>() {
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
        new HashMap<>() {
          {
            put(STARTER_CARD_COORDINATES, starterCard);
          }
        };

    if (starterCard.getPlayedSide() == CardSide.BACK)
      for (Resources resource : starterCard.getCentralResources())
        playerElements.put(resource, playerElements.get(resource) + 1);

    for (Corner corner : starterCard.getActiveCorners().values())
      if (corner.type == CornerTypes.VISIBLE && corner.element.isPresent()) {
        Elements e = corner.element.get();
        playerElements.put(e, playerElements.get(e) + 1);
      }
  }

  /**
   * Allows to draw the list of cards placed around the coordinates given.
   *
   * @param coords coordinates of a position
   * @return A map containing non-null adjacent cards
   */
  public Map<CornerPosition, PlayableCard> adjacentCards(Coords coords) {
    return adjacentCoords(coords).entrySet().stream()
        .filter(entry -> getCard(entry.getValue()) != null)
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> getCard(entry.getValue())));
  }

  /**
   * Allows to draw the list of corners around the coordinates given.
   *
   * @param coords coordinates of a position
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
   * Allows to draw a list of coordinates around the coordinates given.
   *
   * @param coords coordinates of a position
   * @return A map containing the adjacent coordinates
   */
  public Map<CornerPosition, Coords> adjacentCoords(Coords coords) {
    return new HashMap<>() {
      {
        put(CornerPosition.TOP_LEFT, new Coords(coords.x - 1, coords.y + 1));
        put(CornerPosition.TOP_RIGHT, new Coords(coords.x + 1, coords.y + 1));
        put(CornerPosition.BOTTOM_RIGHT, new Coords(coords.x + 1, coords.y - 1));
        put(CornerPosition.BOTTOM_LEFT, new Coords(coords.x - 1, coords.y - 1));
      }
    };
  }

  /**
   * Used to draw all available slots for card placement.
   *
   * @return list of available coords
   */
  public List<Coords> availableCoords() {
    return board.keySet().stream()
            .flatMap(x -> adjacentCoords(x).values().stream())
            .filter(x -> !board.containsKey(x))
            .filter(x -> adjacentCorners(x).values().stream().allMatch(Corner::canPlaceCardAbove))
            .collect(Collectors.toList());
  }

  /**
   * Used to draw whether a card can be placed in a certain position.
   * It checks all adjacent corners and if the player has enough resources to play the card.
   *
   * @param coords coordinates to check
   * @return true if the card can be placed, false otherwise
   */
  public boolean canPlaceCardAt(Coords coords, PlayableCard card, CardSide side) {
    return getCard(coords) == null
        && !adjacentCards(coords).isEmpty()
        && card.enoughResources(playerElements, side)
        && adjacentCorners(coords).values().stream().allMatch(Corner::canPlaceCardAbove);
  }

  /**
   * Updates playerElements after placing the card at coords. Must be called after successfully placing a card.
   *
   * @param playerToken token of the player
   * @param coords coordinates of the placed card
   */
  public void updatePlayerElements(PlayerToken playerToken, Coords coords) {
    // Remove covered items
    adjacentCorners(coords).values().stream()
        .map(x -> x.element)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(
            item -> {
              playerElements.put(item, playerElements.get(item) - 1);
            });

    // Add card type resource
    PlayableCard placedCard = this.getCard(coords);
    if (placedCard.getPlayedSide() == CardSide.BACK) {
      Resources cardResource = placedCard.type.get();
      playerElements.put(cardResource, playerElements.get(cardResource) + 1);
    }

    // Add corner items
    placedCard.getActiveCorners().values().stream()
        .map(x -> x.element)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(
            item -> {
              playerElements.put(item, playerElements.get(item) + 1);
            });

    notify(new PlayerElementsEvent(playerToken, new HashMap<>(playerElements)));
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
   * Sets the given card at the given position and sets adjacent corners to covered.
   *
   * @param playerToken token of the player placing the card
   * @param coords coordinates where to place the card
   * @param card card to place
   * @param cardSide side of the card played
   * @return an event to signal a card has been placed
   */
  public PlayedCardEvent placeCard(PlayerToken playerToken, Coords coords, PlayableCard card, CardSide cardSide) {
    card.playSide(cardSide);
    board.put(coords, card);

    // playability is already checked, so no HIDDEN corners are present
    adjacentCorners(coords).values().forEach(corner -> corner.type = CornerTypes.COVERED);

    PlayedCardEvent event = new PlayedCardEvent(playerToken, card.id, cardSide, coords);
    notify(event);
    return event;
  }
}
