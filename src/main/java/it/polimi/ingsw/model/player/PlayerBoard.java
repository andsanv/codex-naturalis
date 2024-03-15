package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerItems;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the area of the board personal to a single player.
 * @see Player
 */
public class PlayerBoard {
    static final private int BOARD_SIZE_X = 99;
    static final private int BOARD_SIZE_Y = 99;

    /**
     * This attribute holds the starter card's coordinates in the player's board.
     */
    final private Coords STARTER_CARD_COORDS = new Coords(49,49);

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
     * @param starterCard starter card of the player's board
     */
    PlayerBoard(StarterCard starterCard) {
        this.visibleItems = new HashMap<>();
        this.board = new PlayableCard[BOARD_SIZE_X][BOARD_SIZE_Y];
        this.board[STARTER_CARD_COORDS.getX()][STARTER_CARD_COORDS.getY()] = starterCard;
    }

    public boolean playStarterCard(StarterCard starterCard, CardSide side) {

    }

    public boolean playCard(PlayableCard card, Coords coords, CardSide side) {
        boolean placeable = true;

        // TODO ERRORS ENUM
        // TODO LIST OF ERRORS
        // TODO FINISH

        // invalid cell
        if((coords.getX() - coords.getY()) % 2 != 0)
            return false;

        // already occupied
        if(board[coords.getX()][coords.getY()] != null)
            return false;

        // no available adjacent cards
        if((board[coords.getX() - 1][coords.getY() - 1] == null || board[coords.getX() - 1][coords.getY() - 1].getActiveCorners(BR)) &&
           board[coords.getX() - 1][coords.getY() + 1] == null &&
           board[coords.getX() + 1][coords.getY() - 1] == null &&
           board[coords.getX() + 1][coords.getY() + 1] == null
        ) {
            return false;
        }

        if()

        this.board[coords.getX()][coords.getY()] = card;
        return true;
    }

    private Map<CornerPosition, PlayableCard> adjacentCards(Coords c) {
        return getAdjacentCoords(c)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, e -> getCard(e.getValue())
                ));
    }

    private Map<CornerPosition, Corner> adjacentCorners(Coords c) {
        // TODO
        // return adjacentCards(c)
        //         .entrySet()
        //         .stream()
        //         .collect(Collectors.toMap(
        //         ));
    }


    static private Map<CornerPosition, Coords> getAdjacentCoords(Coords c) {
        Map<CornerPosition, Coords> coords = new HashMap<>();

        coords.put(CornerPosition.TOP_LEFT, new Coords(c.x - 1,c.y - 1));
        coords.put(CornerPosition.TOP_RIGHT, new Coords(c.x - 1, c.y + 1));
        coords.put(CornerPosition.BOTTOM_RIGHT, new Coords(c.x + 1, c.y + 1));
        coords.put(CornerPosition.BOTTOM_LEFT, new Coords(c.x + 1, c.y - 1));

        return coords;
    }

    private PlayableCard getCard(Coords coords) {
        return board[coords.getX()][coords.getY()];
    }

    //private List<Corner> adjacentCorners(Coords coords) {
    //    Map<CornerPosition, PlayableCard> cards = adjacentCards(coords);
    //
    //    return Arrays.asList(
    //        cards.get(CornerPosition.TOP_LEFT).getActiveCorners().get(CornerPosition.BOTTOM_RIGHT),
    //        cards.get(CornerPosition.TOP_RIGHT).getActiveCorners().get(CornerPosition.BOTTOM_LEFT),
    //        cards.get(CornerPosition.BOTTOM_RIGHT).getActiveCorners().get(CornerPosition.TOP_LEFT),
    //        cards.get(CornerPosition.BOTTOM_LEFT).getActiveCorners().get(CornerPosition.TOP_RIGHT)
    //    );
    //}
}

enum CardSide {
    FRONT,
    BACK
}

//enum PlayCardResult {
//    SUCCESS_CARD_PLACED,
//    ERROR_COORDINATES_INVALID,
//    ERROR_COORDINATES_ALREADY_IN_USE,
//    ERROR_CANT_PLACE_ON_COVERED_CORNER,
//    ERROR_NOT_ENOUGH_RESOURCE
//}