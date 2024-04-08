package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.corner.Corner;
import it.polimi.ingsw.model.corner.CornerPosition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the area of the board personal to a single player.
 *
 * @see Player
 */
public class PlayerBoard {
    /**
     * This attribute holds the starter card's coordinates in the player's board.
     */
    static final private Coords STARTER_CARD_COORDINATES = new Coords(0, 0);

    /**
     * This attribute represents the player's board
     */
    private Map<Coords, PlayableCard> board;

    /**
     * This map represents the number of items available on the board.
     */
    private Map<Elements, Integer> playerItems;

    /**
     * @param starterCard starter card of the player's board
     */
    PlayerBoard(StarterCard starterCard) {
        this.playerItems = new HashMap<Elements, Integer>() {{
            put(Resources.PLANT, 0);
            put(Resources.ANIMAL, 0);
            put(Resources.FUNGI, 0);
            put(Resources.INSECT, 0);
            put(Items.INKWELL, 0);
            put(Items.QUILL, 0);
            put(Items.MANUSCRIPT, 0);

        }};
        this.board = new HashMap<Coords, PlayableCard>() {{
            put(STARTER_CARD_COORDINATES, starterCard);
        }};
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
     * @param coords coordinates of card
     * @return A map containing non-null adjacent corners
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
     * @param coords coordinates of a point
     * @return A map containing the adjacent slots' coordinates
     */
    static private Map<CornerPosition, Coords> adjacentCoords(Coords coords) {
        return new HashMap<CornerPosition, Coords>() {{
            put(CornerPosition.TOP_LEFT, new Coords(coords.x - 1, coords.y - 1));
            put(CornerPosition.TOP_RIGHT, new Coords(coords.x - 1, coords.y + 1));
            put(CornerPosition.BOTTOM_RIGHT, new Coords(coords.x + 1, coords.y + 1));
            put(CornerPosition.BOTTOM_LEFT, new Coords(coords.x + 1, coords.y - 1));
        }};
    }

    /**
     * @param coords coordinates to check
     * @return True if a card can be placed, false otherwise
     */
    public boolean canPlaceCardAt(Coords coords, PlayableCard card) {
        // TODO maybe delete adjacentCards and adjacentCorners and put all the code in this function
        return !adjacentCards(coords).isEmpty() && card.enoughResources(playerItems) &&
                adjacentCorners(coords).values().stream().allMatch(Corner::canPlaceCardAbove);
    }

    /**
     * Returns the card at the given coordinates.
     * Coords must not be null.
     *
     * @param coords coordinates of the card
     * @return card in given coordinates
     */
    public PlayableCard getCard(Coords coords) {
        return board.get(coords);
    }

    /**
     * Sets the given card at the given position.
     * The params must not be null.
     *
     * @param coords coordinates where to place the card
     * @param card   card being placed
     */
    public void setCard(Coords coords, PlayableCard card) {
        board.put(coords, card);
    }

    /**
     * @return returns a copy of the visible items
     */
    public Map<Elements, Integer> getPlayerItems() {
        return new HashMap<>(playerItems);
    }

    /**
     * @return returns a copy of the board
     */
    public HashMap<Coords, PlayableCard> getBoard() {
        return new HashMap<>(board);
    }
}

