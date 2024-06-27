package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.PointsType;
import it.polimi.ingsw.model.card.ResourceCard;
import it.polimi.ingsw.model.card.StarterCard;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.corner.CornerPosition;
import it.polimi.ingsw.model.corner.CornerTypes;
import it.polimi.ingsw.model.deck.Deck;
import it.polimi.ingsw.model.deck.GoldDeckCreator;
import it.polimi.ingsw.model.deck.ResourceDeckCreator;
import it.polimi.ingsw.model.deck.StarterDeckCreator;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Trio;

/**
 * Use this class for printing cards.
 */
public class CLICardUtils {
    /**
     * Boolean for checking if the decks have been loaded or not.
     */
    private static boolean init = false;

    /**
     * Needed because the deck creator needs an atomic integer to know ids of the
     * event that he generates on the server.
     */
    private static AtomicInteger fakeAtomicInt = new AtomicInteger(0);

    /**
     * Needed because the deck creator needs a list of observers for
     * event that he generates on the server.
     */
    private static List<Observer> fakeObserversList = new ArrayList<>();

    /**
     * A map with the light resource card representation
     */
    private static Map<Integer, LightResourceCard> resourceCards;

    /**
     * A map with the light gold card representation
     */
    private static Map<Integer, LightGoldCard> goldCards;

    /**
     * A map with the light starter card representation
     */
    private static Map<Integer, LightStarterCard> starterCards;

    /**
     * Returns a matrix containing the requested card as Ansi sequences
     * 
     * @param id   the id of the card
     * @param side the side of the card
     * 
     * @return an matrix of Ansi sequences representing the card
     */
    public static Ansi[][] cardToMatrix(int id, CardSide side) {
        if (!init)
            init();

        if (resourceCards.containsKey(id))
            return side == CardSide.FRONT ? resourceCardFront(id) : resourceCardBack(id);
        else if (goldCards.containsKey(id))
            return side == CardSide.FRONT ? goldCardFront(id) : goldCardBack(id);
        else if (starterCards.containsKey(id))
            return side == CardSide.FRONT ? starterCardFront(id) : starterCardBack(id);
        else if (id >= 87 && id <= 102) {
            if (side == CardSide.BACK) {
                Ansi[][] res = simpleCard(YELLOW);
                res[2][4] = colorAndResetFg("O", YELLOW);
                res[2][5] = colorAndResetFg("B", YELLOW);
                res[2][6] = colorAndResetFg("J", YELLOW);
                return res;
            } else if (id <= 94)
                return objectivePatternCardFront(id);
            else
                return elementsPatternCardFront(id);
        }
        return null;
    }

    /**
     * Returns an Ansi sequence of the given string with the foreground colored with
     * the given Ansi.Color
     * 
     * @param string the string to color
     * @param color  the color as Ansi.Color
     * @return the formatted Ansi sequence
     */
    private static Ansi colorAndResetFg(String string, Ansi.Color color) {
        return ansi().reset().fg(color).a(string).reset();
    }

    /**
     * Returns an Ansi sequence of the given char with the foreground colored with
     * the given Ansi.Color
     * 
     * @param string the char to color
     * @param color  the color as Ansi.Color
     * @return the formatted Ansi sequence
     */

    private static Ansi colorAndResetFg(char c, Ansi.Color color) {
        return ansi().reset().fg(color).a(c).reset();
    }

    /**
     * Returns an Ansi sequence of the given string with the background colored with
     * the given Ansi.Color
     * 
     * @param string the string to color
     * @param color  the color as Ansi.Color
     * @return the formatted Ansi sequence
     */
    private static Ansi colorAndResetBg(String string, Ansi.Color color) {
        return ansi().reset().bg(color).a(string).reset();
    }

    /**
     * Creates an empty space ansi sequence without any formatting
     * 
     * @return one empty space as an Ansi sequence
     */
    private static Ansi emptyAnsi() {
        return ansi().reset().a(" ");
    }

    /**
     * Creates a printable matrix with the border of the given color for a playable
     * card. Can be used as helper for printing resource, gold and starter cards.
     * 
     * @param borderColor the color of the border
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] playableCard(Ansi.Color borderColor) {
        Ansi[][] result = new Ansi[5][11];

        String upperBorder = "╭─┬─────┬─╮";
        String lowerBorder = "╰─┴─────┴─╯";

        for (int i = 0; i < 11; i++) {
            result[0][i] = colorAndResetFg(upperBorder.charAt(i), borderColor);
            result[4][i] = colorAndResetFg(lowerBorder.charAt(i), borderColor);
        }

        result[1][0] = colorAndResetFg("│", borderColor);
        result[1][1] = emptyAnsi();
        result[1][2] = colorAndResetFg("│", borderColor);
        result[1][3] = emptyAnsi();
        result[1][4] = emptyAnsi();
        result[1][5] = emptyAnsi();
        result[1][6] = emptyAnsi();
        result[1][7] = emptyAnsi();
        result[1][8] = colorAndResetFg("│", borderColor);
        result[1][9] = emptyAnsi();
        result[1][10] = colorAndResetFg("│", borderColor);

        result[2][0] = colorAndResetFg("├", borderColor);
        result[2][1] = colorAndResetFg("─", borderColor);
        result[2][2] = colorAndResetFg("┤", borderColor);
        result[2][3] = emptyAnsi();
        result[2][4] = emptyAnsi();
        result[2][5] = emptyAnsi();
        result[2][6] = emptyAnsi();
        result[2][7] = emptyAnsi();
        result[2][8] = colorAndResetFg("├", borderColor);
        result[2][9] = colorAndResetFg("─", borderColor);
        result[2][10] = colorAndResetFg("┤", borderColor);

        result[3][0] = colorAndResetFg("│", borderColor);
        result[3][1] = emptyAnsi();
        result[3][2] = colorAndResetFg("│", borderColor);
        result[3][3] = emptyAnsi();
        result[3][4] = emptyAnsi();
        result[3][5] = emptyAnsi();
        result[3][6] = emptyAnsi();
        result[3][7] = emptyAnsi();
        result[3][8] = colorAndResetFg("│", borderColor);
        result[3][9] = emptyAnsi();
        result[3][10] = colorAndResetFg("│", borderColor);

        return result;
    }

    /**
     * Creates a printable matrix with the border of the given color for a simple
     * card. Can be used as helper for printing objective cards or blank cards.
     * 
     * @param borderColor the color of the border
     * @return the card as an Ansi matrix
     */
    public static Ansi[][] simpleCard(Ansi.Color borderColor) {
        Ansi[][] result = new Ansi[5][11];

        String upperBorder = "╭─────────╮";
        String lowerBorder = "╰─────────╯";

        for (int i = 0; i < 11; i++) {
            result[0][i] = colorAndResetFg(upperBorder.charAt(i), borderColor);
            result[4][i] = colorAndResetFg(lowerBorder.charAt(i), borderColor);
        }

        result[1][0] = colorAndResetFg("│", borderColor);
        result[1][1] = emptyAnsi();
        result[1][2] = emptyAnsi();
        result[1][3] = emptyAnsi();
        result[1][4] = emptyAnsi();
        result[1][5] = emptyAnsi();
        result[1][6] = emptyAnsi();
        result[1][7] = emptyAnsi();
        result[1][8] = emptyAnsi();
        result[1][9] = emptyAnsi();
        result[1][10] = colorAndResetFg("│", borderColor);

        result[2][0] = colorAndResetFg("│", borderColor);
        result[2][1] = emptyAnsi();
        result[2][2] = emptyAnsi();
        result[2][3] = emptyAnsi();
        result[2][4] = emptyAnsi();
        result[2][5] = emptyAnsi();
        result[2][6] = emptyAnsi();
        result[2][7] = emptyAnsi();
        result[2][8] = emptyAnsi();
        result[2][9] = emptyAnsi();
        result[2][10] = colorAndResetFg("│", borderColor);

        result[3][0] = colorAndResetFg("│", borderColor);
        result[3][1] = emptyAnsi();
        result[3][2] = emptyAnsi();
        result[3][3] = emptyAnsi();
        result[3][4] = emptyAnsi();
        result[3][5] = emptyAnsi();
        result[3][6] = emptyAnsi();
        result[3][7] = emptyAnsi();
        result[3][8] = emptyAnsi();
        result[3][9] = emptyAnsi();
        result[3][10] = colorAndResetFg("│", borderColor);

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of a resource card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] resourceCardFront(int id) {
        LightResourceCard card = resourceCards.get(id);
        Map<CornerPosition, String> corners = card.corners;

        Ansi.Color borderColor = elementToColor(card.type);

        Ansi[][] result = playableCard(borderColor);

        result[1][1] = colorAndResetFg(corners.get(CornerPosition.TOP_LEFT),
                elementToColor(corners.get(CornerPosition.TOP_LEFT)));
        result[1][5] = ansi().a(card.points != 0 ? card.points : " ");
        result[1][9] = colorAndResetFg(corners.get(CornerPosition.TOP_RIGHT),
                elementToColor(corners.get(CornerPosition.TOP_RIGHT)));

        result[2][5] = colorAndResetFg(card.type, elementToColor(card.type));

        result[3][1] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_LEFT),
                elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)));
        result[1][5] = ansi().a(card.points != 0 ? card.points : " ");
        result[3][9] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_RIGHT),
                elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)));

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the back of a resource card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] resourceCardBack(int id) {
        LightResourceCard card = resourceCards.get(id);

        Ansi.Color borderColor = elementToColor(card.type);

        Ansi[][] result = playableCard(borderColor);

        result[2][5] = colorAndResetFg(card.type, elementToColor(card.type));

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of a gold card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] goldCardFront(int id) {
        LightGoldCard card = goldCards.get(id);
        Map<CornerPosition, String> corners = card.corners;

        Ansi.Color borderColor = elementToColor(card.type);

        Ansi[][] result = playableCard(borderColor);

        result[1][1] = colorAndResetFg(corners.get(CornerPosition.TOP_LEFT),
                elementToColor(corners.get(CornerPosition.TOP_LEFT)));
        result[1][4] = colorAndResetFg(card.points.charAt(0), DEFAULT);
        result[1][5] = colorAndResetFg(card.points.charAt(1), DEFAULT);
        result[1][6] = colorAndResetFg(card.points.charAt(2), DEFAULT);
        result[1][9] = colorAndResetFg(corners.get(CornerPosition.TOP_RIGHT),
                elementToColor(corners.get(CornerPosition.TOP_RIGHT)));

        result[2][5] = colorAndResetBg(card.type, YELLOW);

        result[3][1] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_LEFT),
                elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)));

        String paddedRequiredResources = addPadding(card.required.stream().collect(Collectors.joining()), 5);

        List<Ansi> requiredAsAnsi = IntStream.range(0, paddedRequiredResources.length())
                .mapToObj(i -> Character.toString(paddedRequiredResources.charAt(i)))
                .map(c -> c.equals(" ") ? emptyAnsi() : colorAndResetFg(c, elementToColor(c)))
                .collect(Collectors.toList());

        for (int i = 3; i <= 7; i++) {
            result[3][i] = requiredAsAnsi.get(i - 3);
        }

        result[3][9] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_RIGHT),
                elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)));

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of a gold card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] goldCardBack(int id) {
        LightGoldCard card = goldCards.get(id);

        Ansi.Color borderColor = elementToColor(card.type);

        Ansi[][] result = playableCard(borderColor);

        result[2][5] = colorAndResetBg(card.type, YELLOW);

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of a starter card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] starterCardFront(int id) {
        LightStarterCard card = starterCards.get(id);
        Map<CornerPosition, String> corners = card.cornersFront;

        Ansi[][] result = playableCard(DEFAULT);

        result[1][1] = colorAndResetFg(corners.get(CornerPosition.TOP_LEFT),
                elementToColor(corners.get(CornerPosition.TOP_LEFT)));
        result[1][9] = colorAndResetFg(corners.get(CornerPosition.TOP_RIGHT),
                elementToColor(corners.get(CornerPosition.TOP_RIGHT)));

        String paddedRequiredResources = addPadding(card.resources.stream().collect(Collectors.joining()), 3);

        List<Ansi> requiredAsAnsi = IntStream.range(0, paddedRequiredResources.length())
                .mapToObj(i -> Character.toString(paddedRequiredResources.charAt(i)))
                .map(c -> c.equals(" ") ? emptyAnsi() : colorAndResetFg(c, elementToColor(c)))
                .collect(Collectors.toList());

        for (int i = 1; i <= 3; i++) {
            result[i][5] = requiredAsAnsi.get(i - 1);
        }

        result[3][1] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_LEFT),
                elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)));
        result[3][9] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_RIGHT),
                elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)));

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the back of a starter card
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] starterCardBack(int id) {
        LightStarterCard card = starterCards.get(id);
        Map<CornerPosition, String> corners = card.cornersBack;

        Ansi[][] result = playableCard(DEFAULT);

        result[1][1] = colorAndResetFg(corners.get(CornerPosition.TOP_LEFT),
                elementToColor(corners.get(CornerPosition.TOP_LEFT)));
        result[1][9] = colorAndResetFg(corners.get(CornerPosition.TOP_RIGHT),
                elementToColor(corners.get(CornerPosition.TOP_RIGHT)));
        result[3][1] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_LEFT),
                elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)));
        result[3][9] = colorAndResetFg(corners.get(CornerPosition.BOTTOM_RIGHT),
                elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)));

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of an objective card with a
     * pattern
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] objectivePatternCardFront(int id) {
        List<Ansi.Color[][]> patterns = Arrays.asList(
                new Ansi.Color[][] { { DEFAULT, DEFAULT, RED }, { DEFAULT, RED, DEFAULT }, { RED, DEFAULT, DEFAULT } },
                new Ansi.Color[][] { { GREEN, DEFAULT, DEFAULT }, { DEFAULT, GREEN, DEFAULT },
                        { DEFAULT, DEFAULT, GREEN } },
                new Ansi.Color[][] { { DEFAULT, DEFAULT, BLUE }, { DEFAULT, BLUE, DEFAULT },
                        { BLUE, DEFAULT, DEFAULT } },
                new Ansi.Color[][] { { MAGENTA, DEFAULT, DEFAULT }, { DEFAULT, MAGENTA, DEFAULT },
                        { DEFAULT, DEFAULT, MAGENTA } },
                new Ansi.Color[][] { { DEFAULT, RED, DEFAULT }, { DEFAULT, RED, DEFAULT },
                        { DEFAULT, DEFAULT, GREEN } },
                new Ansi.Color[][] { { DEFAULT, GREEN, DEFAULT }, { DEFAULT, GREEN, DEFAULT },
                        { MAGENTA, DEFAULT, DEFAULT } },
                new Ansi.Color[][] { { DEFAULT, DEFAULT, RED }, { DEFAULT, BLUE, DEFAULT },
                        { DEFAULT, BLUE, DEFAULT } },
                new Ansi.Color[][] { { BLUE, DEFAULT, DEFAULT }, { DEFAULT, MAGENTA, DEFAULT },
                        { DEFAULT, MAGENTA, DEFAULT } });
        List<Integer> points = Arrays.asList(2, 2, 2, 2, 3, 3, 3, 3);

        Ansi[][] result = simpleCard(YELLOW);

        result[2][3] = colorAndResetFg(points.get(id - 87).toString(), DEFAULT);

        for (int i = 1; i <= 3; i++)
            for (int j = 6; j <= 8; j++) {
                Ansi.Color color = patterns.get(id - 87)[i - 1][j - 6];
                result[i][j] = color != DEFAULT ? colorAndResetFg("█", color) : emptyAnsi();
            }

        return result;
    }

    /**
     * Creates an Ansi matrix for printing the front of an objective card with
     * required elements
     * 
     * @param id the id of the card
     * @return the card as an Ansi matrix
     */
    private static Ansi[][] elementsPatternCardFront(int id) {
        List<String[]> elements = Arrays.asList(
                new String[] { "F", "F", "F" },
                new String[] { "P", "P", "P" },
                new String[] { "A", "A", "A" },
                new String[] { "I", "I", "I" },
                new String[] { "Q", "K", "M" },
                new String[] { "M", "M", " " },
                new String[] { "K", "K", " " },
                new String[] { "Q", "Q", " " });

        List<Integer> points = Arrays.asList(2, 2, 2, 2, 3, 2, 2, 2);

        Ansi[][] result = simpleCard(YELLOW);

        result[2][3] = colorAndResetFg(points.get(id - 94).toString(), DEFAULT);

        for (int j = 5; j <= 7; j++) {
            String element = elements.get(id - 94)[j - 5];
            result[2][j] = !element.equals(" ") ? colorAndResetFg(element, elementToColor(element)) : emptyAnsi();
        }

        return result;

    }

    /**
     * Creates a card that is a placeholder for another one.
     * 
     * @param n the number associated to the placeholder
     * @return the card as an Ansi matrix
     */
    public static Ansi[][] placeholderCard(int n) {
        Ansi[][] placeholder = simpleCard(WHITE);

        String paddedNumber = addPadding(Integer.toString(n), 5);

        for (int i = 0; i <= 4; i++) {
            placeholder[2][3 + i] = colorAndResetFg(paddedNumber.charAt(i), WHITE);
        }

        return placeholder;
    }

    /**
     * Returns a matrix of Ansi sequences that represents the board of a player.
     * 
     * @param playerCards the cards of the player
     * @return an Ansi sequence matrix
     */
    public static Ansi[][] createBoard(Map<Integer, Trio<Integer, CardSide, Coords>> playerCards) {
        if (playerCards.isEmpty())
            return null;

        // Find how big is the matrix to return
        int x_max = Integer.MIN_VALUE;
        int x_min = Integer.MAX_VALUE;
        int y_max = Integer.MIN_VALUE;
        int y_min = Integer.MAX_VALUE;

        List<Coords> coords = playerCards.values().stream().map(trio -> trio.third).collect(Collectors.toList());

        for (Coords c : coords) {
            if (c.x > x_max)
                x_max = c.x;
            if (c.x < x_min)
                x_min = c.x;
            if (c.y > y_max)
                y_max = c.y;
            if (c.y < y_min)
                y_min = c.y;
        }

        System.out.println(x_min + " " + x_max);
        System.out.println(y_min + " " + y_max);

        Ansi[][] board = emptyAnsiMatrix(5 + (y_max - y_min) * 3, 11 + (x_max - x_min) * 8);

        int current = 0;

        while (playerCards.containsKey(current)) {
            Trio<Integer, CardSide, Coords> placement = playerCards.get(current);
            Coords c = placement.third;
            System.out.println(c);

            int j_pos = 0;
            int i_pos = 0;

            if (c.x != x_min) {
                i_pos = 8 * (c.x - x_min);
            }

            if (c.y != y_max) {
                j_pos = 3 * (y_max - c.y);
            }

            addCardToMatrix(board, cardToMatrix(placement.first, placement.second), j_pos, i_pos);

            current++;
        }

        return board;
    }

    /**
     * Returns a matrix of Ansi sequences that represents the board of a player with
     * the cards possible placements highlighted.
     * 
     * @param playerCards      the cards of the player
     * @param placeholderSlots slots where the player can place the card
     * @return an Ansi sequence matrix of the board with the available slots
     *         highlighted
     */
    public static Ansi[][] createBoardWithPlayability(Map<Integer, Trio<Integer, CardSide, Coords>> playerCards,
            Map<Integer, Coords> placeholderSlots) {
        if (playerCards.isEmpty())
            return null;

        // Find how big is the matrix to return
        int x_max = Integer.MIN_VALUE;
        int x_min = Integer.MAX_VALUE;
        int y_max = Integer.MIN_VALUE;
        int y_min = Integer.MAX_VALUE;

        List<Coords> coords = playerCards.values().stream().map(trio -> trio.third).collect(Collectors.toList());
        placeholderSlots.values().stream().forEach(c -> coords.add(c));

        for (Coords c : coords) {
            if (c.x > x_max)
                x_max = c.x;
            if (c.x < x_min)
                x_min = c.x;
            if (c.y > y_max)
                y_max = c.y;
            if (c.y < y_min)
                y_min = c.y;
        }

        System.out.println(x_min + " " + x_max);
        System.out.println(y_min + " " + y_max);

        Ansi[][] board = emptyAnsiMatrix(5 + (y_max - y_min) * 3, 11 + (x_max - x_min) * 8);

        int current = 0;

        while (playerCards.containsKey(current)) {
            Trio<Integer, CardSide, Coords> placement = playerCards.get(current);
            Coords c = placement.third;

            int j_pos = 0;
            int i_pos = 0;

            if (c.x != x_min) {
                i_pos = 8 * (c.x - x_min);
            }

            if (c.y != y_max) {
                j_pos = 3 * (y_max - c.y);
            }

            addCardToMatrix(board, cardToMatrix(placement.first, placement.second), j_pos, i_pos);

            current++;
        }

        for (Entry<Integer, Coords> placeholder : placeholderSlots.entrySet()) {
            Coords c = placeholder.getValue();
            
            int j_pos = 0;
            int i_pos = 0;

            if (c.x != x_min) {
                i_pos = 8 * (c.x - x_min);
            }

            if (c.y != y_max) {
                j_pos = 3 * (y_max - c.y);
            }

            addCardToMatrix(board, CLICardUtils.placeholderCard(current), j_pos, i_pos);

        }

        return board;
    }

    /**
     * Adds the give card to an Ansi matrix.
     * 
     * @param matrix         the Ansi matrix
     * @param card           the card as an Ansi matrix
     * @param startingRow    the row where to start adding the card
     * @param startingColumn the column where to start adding the card
     */
    public static void addCardToMatrix(Ansi[][] matrix, Ansi[][] card, int startingRow, int startingColumn) {
        for (int i = 0; i < card.length; i++)
            for (int j = 0; j < card[0].length; j++)
                matrix[startingRow + i][startingColumn + j] = card[i][j];
    }

    /**
     * Returns an empty ansi matrix of the given size
     * 
     * @param rows    number of rows
     * @param columns number of columns
     * 
     * @return the Ansi matrix
     */
    public static Ansi[][] emptyAnsiMatrix(int rows, int columns) {
        Ansi[][] matrix = new Ansi[rows][columns];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                matrix[i][j] = emptyAnsi();

        return matrix;
    }

    /**
     * Adds padding to the given string.
     * The string must be shorter or equal to the desired length.
     * 
     * @param string        the string to add padding to
     * @param desiredLenght the desired length of the padded string
     * @return the padded string
     */
    private static String addPadding(String string, int desiredLenght) {
        int padding = desiredLenght - string.length();

        if (padding <= 0)
            return string;

        int leftPadding;
        int righPadding;

        if (padding % 2 == 0) {
            leftPadding = righPadding = padding / 2;
        } else {
            leftPadding = padding / 2;
            righPadding = leftPadding + 1;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < leftPadding; i++)
            result.append(' ');

        result.append(string);

        for (int i = 0; i < righPadding; i++)
            result.append(' ');

        return result.toString();
    }

    /**
     * Associates a color to an element of the cart to print.
     * 
     * @param element the element to color
     * @return the color to use
     */
    private static Ansi.Color elementToColor(String element) {
        switch (element) {
            case "P":
                return GREEN;
            case "A":
                return BLUE;
            case "I":
                return MAGENTA;
            case "F":
                return RED;
            case "K":
            case "M":
            case "Q":
                return YELLOW;
            default:
                return DEFAULT;
        }
    }

    private static void init() {
        loadResourceCards();
        loadGoldCards();
        loadStarterCards();

        init = true;
    }

    /**
     * Loads resource cards
     */
    private static void loadResourceCards() {
        resourceCards = new HashMap<>();
        Deck<ResourceCard> deck = ResourceDeckCreator.createDeck(fakeObserversList, fakeAtomicInt);

        while (!deck.isEmpty()) {
            ResourceCard card = deck.draw(PlayerToken.RED, 0).get();
            resourceCards.put(card.id, new LightResourceCard(
                    card.type.get().toString().substring(0, 1),
                    card.getFrontCorners().entrySet().stream().collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        if (e.getValue().type.equals(CornerTypes.HIDDEN))
                                            return "-";
                                        else if (e.getValue().element.isPresent())
                                            return e.getValue().element.get().equals(Items.INKWELL) ? "K"
                                                    : e.getValue().element.get().toString().substring(0, 1);
                                        else
                                            return " ";
                                    })),
                    card.pointsType.equals(PointsType.ONE) ? 1 : 0));
        }
    }

    /**
     * Loads gold cards
     */
    private static void loadGoldCards() {
        goldCards = new HashMap<>();
        Deck<GoldCard> deck = GoldDeckCreator.createDeck(fakeObserversList, fakeAtomicInt);

        while (!deck.isEmpty()) {
            GoldCard card = deck.draw(PlayerToken.RED, 0).get();

            String pointsType;

            switch (card.pointsType) {
                case THREE:
                    pointsType = " 3 ";
                    break;
                case FIVE:
                    pointsType = " 5 ";
                    break;
                case ONE_PER_QUILL:
                    pointsType = "1*Q";
                    break;
                case ONE_PER_INKWELL:
                    pointsType = "1*K";
                    break;
                case ONE_PER_MANUSCRIPT:
                    pointsType = "1*M";
                    break;
                case TWO_PER_COVERED_CORNER:
                    pointsType = "2*C";
                    break;
                default:
                    pointsType = "   ";
                    break;
            }

            goldCards.put(card.id, new LightGoldCard(
                    card.type.get().toString().substring(0, 1),
                    card.getFrontCorners().entrySet().stream().collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        if (e.getValue().type.equals(CornerTypes.HIDDEN))
                                            return "-";
                                        else if (e.getValue().element.isPresent())
                                            return e.getValue().element.get().equals(Items.INKWELL) ? "K"
                                                    : e.getValue().element.get().toString().substring(0, 1);
                                        else
                                            return " ";
                                    })),
                    pointsType,
                    card.getRequiredResources().entrySet().stream()
                            .flatMap(e -> e.getKey().toString().substring(0, 1).repeat(e.getValue()).chars()
                                    .mapToObj(c -> String.valueOf((char) c)))
                            .collect(Collectors.toList())));
        }
    }

    /**
     * Loads starter cards
     */
    private static void loadStarterCards() {
        starterCards = new HashMap<>();
        Deck<StarterCard> deck = StarterDeckCreator.createDeck(fakeObserversList, fakeAtomicInt);

        while (!deck.isEmpty()) {
            StarterCard card = deck.draw(PlayerToken.RED, 0).get();
            starterCards.put(card.id, new LightStarterCard(
                    card.getCentralResources().stream().map(res -> res.toString().substring(0, 1)).sorted()
                            .collect(Collectors.toList()),
                    card.getFrontCorners().entrySet().stream().collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        if (e.getValue().element.isPresent())
                                            return e.getValue().element.get().toString().substring(0, 1);
                                        else
                                            return " ";
                                    })),
                    card.getBackCorners().entrySet().stream().collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> {
                                        if (e.getValue().element.isPresent())
                                            return e.getValue().element.get().toString().substring(0, 1);
                                        else
                                            return " ";
                                    }))));
        }
    }
}

/**
 * Simple resource card representation
 */
class LightResourceCard {
    /**
     * The card's resource
     */
    final String type;

    /**
     * The elements in the corners
     */
    final Map<CornerPosition, String> corners;

    /**
     * The awarded points
     */
    final int points;

    /**
     * Constructor of the light resource card
     * 
     * @param type    the card resources
     * @param corners the corners
     * @param points  the awarded points
     */
    public LightResourceCard(String type, Map<CornerPosition, String> corners, int points) {
        this.type = type;
        this.corners = corners;
        this.points = points;
    }
}

/**
 * Simple gold card representation
 */
class LightGoldCard {
    /**
     * The card's resource
     */
    final String type;

    /**
     * The elements in the corners
     */
    final Map<CornerPosition, String> corners;

    /**
     * The awarded points
     */
    final String points;

    /**
     * The required elements to place the card
     */
    final List<String> required;

    /**
     * Constructor of the light gold card
     * 
     * @param type     the card resources
     * @param corners  the corners
     * @param points   the awarded points
     * @param required required elements to place the card
     */
    public LightGoldCard(String type, Map<CornerPosition, String> corners, String points, List<String> required) {
        this.type = type;
        this.corners = corners;
        this.points = points;
        this.required = required;
    }
}

/**
 * Simple starter card representation
 */
class LightStarterCard {
    /**
     * The card's center resources
     */
    final List<String> resources;

    /**
     * The front corners
     */
    final Map<CornerPosition, String> cornersFront;

    /**
     * The back corners
     */
    final Map<CornerPosition, String> cornersBack;

    /**
     * Constructor of the light starter card
     * 
     * @param resources    the central resources
     * @param cornersFront the front corners
     * @param cornersBack  the back corners
     */
    public LightStarterCard(List<String> resources, Map<CornerPosition, String> cornersFront,
            Map<CornerPosition, String> cornersBack) {
        this.resources = resources;
        this.cornersFront = cornersFront;
        this.cornersBack = cornersBack;
    }
}