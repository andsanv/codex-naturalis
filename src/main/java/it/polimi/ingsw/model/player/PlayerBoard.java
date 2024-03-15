package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.HiddenCorner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the area of the board personal to a single player.
 *
 * @see Player
 */
public class PlayerBoard {
    static final private int BOARD_SIZE_X = 99;
    static final private int BOARD_SIZE_Y = 99;

    /**
     * This attribute holds the starter card's coordinates in the player's board.
     */
    static final private Coords STARTER_CARD_COORDS = new Coords(49, 49);

    /**
     * This map represents the number of items available on the board.
     */
    private Map<CornerItems, Integer> visibleItems;

    /**
     * This matrix is the player's board's inner representation.
     */
    private PlayableCard[][] board;

    /**
     * TODO: add description
     *
     * @param starterCard starter card of the player's board
     */
    PlayerBoard(StarterCard starterCard) {
        this.visibleItems = new HashMap<>();
        this.board = new PlayableCard[BOARD_SIZE_X][BOARD_SIZE_Y];
        this.board[STARTER_CARD_COORDS.x][STARTER_CARD_COORDS.y] = starterCard;
    }

    public PlayCardResult playCard(PlayableCard card, Coords coords, CardSide side) {
        boolean placeable = true;

        // TODO ERRORS ENUM
        // TODO LIST OF ERRORS
        // TODO FINISH

        // Coordinates already occupied
        if (getCard(coords) != null)
            return PlayCardResult.ERROR_COORDINATES_ALREADY_OCCUPIED;

        // No adjacent cards
        if (adjacentCards(coords).isEmpty())
            return PlayCardResult.ERROR_NO_ADJACENT_CARDS;

        Map<CornerPosition, Corner> adjCorners = adjacentCorners(coords);
        // Hidden corner
        if (adjCorners.values().stream().anyMatch(corner -> corner instanceof HiddenCorner))
            return PlayCardResult.ERROR_CANT_PLACE_ON_HIDDEN_CORNER;

        // Not enough resources
        if (card instanceof GoldCard) {
            Map<Resources, Integer> requiredResources = ((GoldCard) card).getRequiredResources();

            if (visibleItems.getOrDefault(CornerItems.PLANT, 0) < requiredResources.getOrDefault(Resources.PLANT, 0) ||
                    visibleItems.getOrDefault(CornerItems.ANIMAL, 0) < requiredResources.getOrDefault(Resources.ANIMAL, 0) ||
                    visibleItems.getOrDefault(CornerItems.FUNGI, 0) < requiredResources.getOrDefault(Resources.FUNGI, 0) ||
                    visibleItems.getOrDefault(CornerItems.INSECT, 0) < requiredResources.getOrDefault(Resources.INSECT, 0)
            )
                return PlayCardResult.ERROR_NOT_ENOUGH_RESOURCES;

        }

        // Add card to the board
        setCard(card, coords);

        // TODO Change corners if they become covered, update visible items by removing the covered ones and adding the new card's ones.
        // TODO update activeCorners

        return PlayCardResult.SUCCESS;
    }

    /**
     * TODO add description
     *
     * @param coords coordinates of card
     * @return A map containing adjacent cards
     */
    private Map<CornerPosition, PlayableCard> adjacentCards(Coords coords) {
        return adjacentCoords(coords)
                .entrySet()
                .stream()
                .filter(entry -> getCard(entry.getValue()) != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getCard(entry.getValue())
                ));
    }

    /**
     * TODO add description
     *
     * @param coords coordinates of card
     * @return A map containing adjacent corners
     */
    private Map<CornerPosition, Corner> adjacentCorners(Coords coords) {
        return adjacentCoords(coords)
                .entrySet()
                .stream()
                .filter(entry -> getCard(entry.getValue()) != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getCard(entry.getValue()).getActiveCorners().get(entry.getKey())
                ));
    }

    /**
     * TODO add description
     *
     * @param coords coordinates of a point
     * @return A map containing the adjacent points' coordinates
     */
    static private Map<CornerPosition, Coords> adjacentCoords(Coords coords) {
        Map<CornerPosition, Coords> map = new HashMap<>();

        map.put(CornerPosition.TOP_LEFT, new Coords(coords.x - 1, coords.y - 1));
        map.put(CornerPosition.TOP_RIGHT, new Coords(coords.x - 1, coords.y + 1));
        map.put(CornerPosition.BOTTOM_RIGHT, new Coords(coords.x + 1, coords.y + 1));
        map.put(CornerPosition.BOTTOM_LEFT, new Coords(coords.x + 1, coords.y - 1));

        return map;
    }

    private PlayableCard getCard(Coords coords) {
        return board[coords.x][coords.y];
    }

    private void setCard(PlayableCard card, Coords coords) {
        board[coords.x][coords.y] = card;
    }
}

