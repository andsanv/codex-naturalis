package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

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
import it.polimi.ingsw.model.player.PlayerToken;

/**
 * Use this class for printing cards.
 * At most one thread can use this class at a time.
 */
public class CLICardUtils {
    public static void main(String[] args) {
        // Test to see the printed card
        init();
        AnsiConsole.systemInstall();
        System.out.println(ansi().eraseScreen());
        print(5, 0, 33, CardSide.BACK);
        print(5, 15, 22, CardSide.FRONT);
        print(10, 0, 51, CardSide.BACK);
        print(10, 15, 54, CardSide.FRONT);
        print(15, 0, 81, CardSide.BACK);
        print(15, 15, 81, CardSide.FRONT);
        print(20, 0, 82, CardSide.BACK);
        print(20, 15, 82, CardSide.FRONT);
        print(25, 0, 83, CardSide.BACK);
        print(25, 15, 83, CardSide.FRONT);

        printBlank(5, 30, 12);

        print(10, 30, 87, CardSide.FRONT);
        print(15, 30, 88, CardSide.FRONT);
        print(20, 30, 89, CardSide.FRONT);
        print(25, 30, 90, CardSide.FRONT);

        print(5, 45, 87, CardSide.BACK);

        print(10, 45, 91, CardSide.FRONT);
        print(15, 45, 92, CardSide.FRONT);
        print(20, 45, 93, CardSide.FRONT);
        print(25, 45, 94, CardSide.FRONT);

        print(10, 60, 95, CardSide.FRONT);
        print(15, 60, 96, CardSide.FRONT);
        print(20, 60, 97, CardSide.FRONT);
        print(25, 60, 98, CardSide.FRONT);

        print(10, 75, 99, CardSide.FRONT);
        print(15, 75, 100, CardSide.FRONT);
        print(20, 75, 101, CardSide.FRONT);
        print(25, 75, 102, CardSide.FRONT);

        AnsiConsole.systemUninstall();
    }

    private static boolean init = false;
    private static AtomicInteger atomicInt = new AtomicInteger(0);
    private static List<Observer> observersList = new ArrayList<>();

    private static Map<Integer, LightResourceCard> resourceCards;
    private static Map<Integer, LightGoldCard> goldCards;
    private static Map<Integer, LightStarterCard> starterCards;

    /**
     * This method is used to draw a card on screen at the given coordinates.
     * The cards will be printed using the following shape or a similar one
     * ╭─┬─────┬─╮
     * │Q│ 3 C │X│
     * ├─┤  F  ├─┤
     * │A│ FFA │ │
     * ╰─┴─────┴─╯
     * 
     * @param r    row of first character
     * @param c    column of first character
     * @param id   id of the card to print
     * @param side the side to print
     */
    public static void print(int r, int c, int id, CardSide side) {
        if (!init)
            return;

        if (resourceCards.containsKey(id)) {
            LightResourceCard card = resourceCards.get(id);
            Ansi.Color borderColor = elementToColor(card.type);
            if (side == CardSide.FRONT) {
                Map<CornerPosition, String> corners = card.corners;

                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .fg(borderColor)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_LEFT)))
                                .a(corners.get(CornerPosition.TOP_LEFT))
                                .fg(borderColor)
                                .a("│  ").reset().a(card.points != 0 ? card.points : " ").fg(borderColor).a("  │")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_RIGHT)))
                                .a(corners.get(CornerPosition.TOP_RIGHT))
                                .fg(borderColor)
                                .a("│")
                                .cursor(r + 2, c)
                                .a("├─┤  ")
                                .reset()
                                .fg(elementToColor(card.type))
                                .a(card.type)
                                .reset()
                                .fg(borderColor)
                                .a("  ├─┤")
                                .cursor(r + 3, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)))
                                .a(corners.get(CornerPosition.BOTTOM_LEFT))
                                .fg(borderColor)
                                .a("│     │")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)))
                                .a(corners.get(CornerPosition.BOTTOM_RIGHT))
                                .fg(borderColor)
                                .a("│")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            } else {
                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .fg(borderColor)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│ │     │ │")
                                .cursor(r + 2, c)
                                .a("├─┤  ")
                                .reset()
                                .fg(elementToColor(card.type))
                                .a(card.type)
                                .reset()
                                .fg(borderColor)
                                .a("  ├─┤")
                                .cursor(r + 3, c)
                                .a("│ │     │ │")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            }
        } else if (goldCards.containsKey(id)) {
            LightGoldCard card = goldCards.get(id);
            Ansi.Color borderColor = elementToColor(card.type);
            if (side == CardSide.FRONT) {
                Map<CornerPosition, String> corners = card.corners;

                String req = card.required.stream()
                        .map(s -> "@|" + elementToColor(s).toString().toLowerCase() + " " + s + "|@")
                        .collect(Collectors.joining());

                int left_padding;
                int righ_padding;

                int space_left = 5 - card.required.size();

                if (space_left % 2 == 0) {
                    left_padding = righ_padding = space_left / 2;
                } else {
                    left_padding = space_left / 2;
                    righ_padding = left_padding + 1;
                }

                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .fg(borderColor)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_LEFT)))
                                .a(corners.get(CornerPosition.TOP_LEFT))
                                .reset()
                                .fg(borderColor)
                                .a("│ " + card.points + " │")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_RIGHT)))
                                .a(corners.get(CornerPosition.TOP_RIGHT))
                                .reset()
                                .fg(borderColor)
                                .a("│")
                                .cursor(r + 2, c)
                                .a("├─┤  ")
                                .bg(YELLOW)
                                .fg(elementToColor(card.type))
                                .a(card.type)
                                .reset()
                                .fg(borderColor)
                                .a("  ├─┤")
                                .cursor(r + 3, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)))
                                .a(corners.get(CornerPosition.BOTTOM_LEFT))
                                .reset()
                                .fg(borderColor)
                                .a("│" + " ".repeat(left_padding))
                                .render(req)
                                .reset()
                                .fg(borderColor)
                                .a(" ".repeat(righ_padding) + "│")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)))
                                .a(corners.get(CornerPosition.BOTTOM_RIGHT))
                                .fg(borderColor)
                                .a("│")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            } else {
                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .fg(borderColor)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│ │     │ │")
                                .cursor(r + 2, c)
                                .a("├─┤  ")
                                .bg(YELLOW)
                                .fg(elementToColor(card.type))
                                .a(card.type)
                                .reset()
                                .fg(borderColor)
                                .a("  ├─┤")
                                .cursor(r + 3, c)
                                .a("│ │     │ │")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            }
        } else if (starterCards.containsKey(id)) {
            LightStarterCard card = starterCards.get(id);
            if (side == CardSide.FRONT) {
                Map<CornerPosition, String> corners = card.cornersFront;

                String req = card.resources.stream()
                        .map(s -> "@|" + elementToColor(s).toString().toLowerCase() + " " + s + "|@")
                        .collect(Collectors.joining());

                int left_padding;
                int righ_padding;

                int space_left = 5 - card.resources.size();

                if (space_left % 2 == 0) {
                    left_padding = righ_padding = space_left / 2;
                } else {
                    left_padding = space_left / 2;
                    righ_padding = left_padding + 1;
                }

                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_LEFT)))
                                .a(corners.get(CornerPosition.TOP_LEFT))
                                .reset()
                                .a("│     │")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_RIGHT)))
                                .a(corners.get(CornerPosition.TOP_RIGHT))
                                .reset()
                                .a("│")
                                .cursor(r + 2, c)
                                .a("├─┤" + " ".repeat(left_padding))
                                .render(req)
                                .reset()
                                .a(" ".repeat(righ_padding) + "├─┤")
                                .reset()
                                .cursor(r + 3, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)))
                                .a(corners.get(CornerPosition.BOTTOM_LEFT))
                                .reset()
                                .a("│     │")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)))
                                .a(corners.get(CornerPosition.BOTTOM_RIGHT))
                                .reset()
                                .a("│")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            } else {
                Map<CornerPosition, String> corners = card.cornersBack;

                System.out.print(
                        ansi()
                                .cursor(r, c)
                                .a("╭─┬─────┬─╮")
                                .cursor(r + 1, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_LEFT)))
                                .a(corners.get(CornerPosition.TOP_LEFT))
                                .reset()
                                .a("│     │")
                                .fg(elementToColor(corners.get(CornerPosition.TOP_RIGHT)))
                                .a(corners.get(CornerPosition.TOP_RIGHT))
                                .reset()
                                .a("│")
                                .cursor(r + 2, c)
                                .a("├─┤     ├─┤")
                                .reset()
                                .cursor(r + 3, c)
                                .a("│")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_LEFT)))
                                .a(corners.get(CornerPosition.BOTTOM_LEFT))
                                .reset()
                                .a("│     │")
                                .fg(elementToColor(corners.get(CornerPosition.BOTTOM_RIGHT)))
                                .a(corners.get(CornerPosition.BOTTOM_RIGHT))
                                .reset()
                                .a("│")
                                .cursor(r + 4, c)
                                .a("╰─┴─────┴─╯")
                                .reset());
            }
        } else if (id >= 87 && id <= 102) {
            if (side == CardSide.FRONT)
                switch (id) {
                    case 87:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { DEFAULT, DEFAULT, RED },
                                { DEFAULT, RED, DEFAULT }, { RED, DEFAULT, DEFAULT } }, 2);
                        break;
                    case 88:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { GREEN, DEFAULT, DEFAULT },
                                { DEFAULT, GREEN, DEFAULT }, { DEFAULT, DEFAULT, GREEN } }, 2);
                        break;
                    case 89:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { DEFAULT, DEFAULT, BLUE },
                                { DEFAULT, BLUE, DEFAULT }, { BLUE, DEFAULT, DEFAULT } }, 2);
                        break;
                    case 90:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { MAGENTA, DEFAULT, DEFAULT },
                                { DEFAULT, MAGENTA, DEFAULT }, { DEFAULT, DEFAULT, MAGENTA } }, 2);
                        break;
                    case 91:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { DEFAULT, RED, DEFAULT },
                                { DEFAULT, RED, DEFAULT }, { DEFAULT, DEFAULT, GREEN } }, 3);
                        break;
                    case 92:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { DEFAULT, GREEN, DEFAULT },
                                { DEFAULT, GREEN, DEFAULT }, { MAGENTA, DEFAULT, DEFAULT } }, 3);
                        break;
                    case 93:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { DEFAULT, DEFAULT, RED },
                                { DEFAULT, BLUE, DEFAULT }, { DEFAULT, BLUE, DEFAULT } }, 3);
                        break;
                    case 94:
                        objectivePatternPrinter(r, c, new Ansi.Color[][] { { BLUE, DEFAULT, DEFAULT },
                                { DEFAULT, MAGENTA, DEFAULT }, { DEFAULT, MAGENTA, DEFAULT } }, 3);
                        break;
                    case 95:
                        objectiveElementsPrinter(r, c, new String[] { "F", "F", "F" }, 2);
                        break;
                    case 96:
                        objectiveElementsPrinter(r, c, new String[] { "P", "P", "P" }, 2);
                        break;
                    case 97:
                        objectiveElementsPrinter(r, c, new String[] { "A", "A", "A" }, 2);
                        break;
                    case 98:
                        objectiveElementsPrinter(r, c, new String[] { "I", "I", "I" }, 2);
                        break;
                    case 99:
                        objectiveElementsPrinter(r, c, new String[] { "Q", "K", "M" }, 3);
                        break;
                    case 100:
                        objectiveElementsPrinter(r, c, new String[] { "M", "M" }, 2);
                        break;
                    case 101:
                        objectiveElementsPrinter(r, c, new String[] { "K", "K" }, 2);
                        break;
                    case 102:
                        objectiveElementsPrinter(r, c, new String[] { "Q", "Q" }, 2);
                        break;
                    default:
                        break;
                }
            else
                System.out.println(
                        ansi()
                                .fg(YELLOW)
                                .cursor(r, c)
                                .a("╭─────────╮")
                                .cursor(r + 1, c)
                                .a("│         │")
                                .cursor(r + 2, c)
                                .a("│   ")
                                .fg(DEFAULT)
                                .a("OBJ")
                                .fg(YELLOW)
                                .a("   │")
                                .cursor(r + 3, c)
                                .a("│         │")
                                .cursor(r + 4, c)
                                .a("╰─────────╯")
                                .reset());
        }

        System.out.flush();
    }

    /**
     * Helper class to print a generic 3x3 pattern objective card as the following
     * ╭─────────╮
     * │     █   │
     * │  2   █  │
     * │       █ │
     * ╰─────────╯
     * 
     * @param r      row of first character
     * @param c      column of first character
     * @param colors colors array of the pattern
     * @param points awarded points of the objective
     */
    private static void objectivePatternPrinter(int r, int c, Ansi.Color[][] colors, int points) {
        System.out.println(
                ansi()
                        .cursor(r, c)
                        .fg(YELLOW)
                        .a("╭─────────╮")
                        .cursor(r + 1, c)
                        .a("│     ")
                        .fg(colors[0][0])
                        .a(colors[0][0] != DEFAULT ? "█" : " ")
                        .fg(colors[0][1])
                        .a(colors[0][1] != DEFAULT ? "█" : " ")
                        .fg(colors[0][2])
                        .a(colors[0][2] != DEFAULT ? "█" : " ")
                        .fg(YELLOW)
                        .a(" │")
                        .cursor(r + 2, c)
                        .a("│  ").fg(DEFAULT).a(points + "  ")
                        .fg(colors[1][0])
                        .a(colors[1][0] != DEFAULT ? "█" : " ")
                        .fg(colors[1][1])
                        .a(colors[1][1] != DEFAULT ? "█" : " ")
                        .fg(colors[1][2])
                        .a(colors[1][2] != DEFAULT ? "█" : " ")
                        .fg(YELLOW)
                        .a(" │")
                        .cursor(r + 3, c)
                        .a("│     ")
                        .fg(colors[2][0])
                        .a(colors[2][0] != DEFAULT ? "█" : " ")
                        .fg(colors[2][1])
                        .a(colors[2][1] != DEFAULT ? "█" : " ")
                        .fg(colors[2][2])
                        .a(colors[2][2] != DEFAULT ? "█" : " ")
                        .fg(YELLOW)
                        .a(" │")
                        .cursor(r + 4, c)
                        .a("╰─────────╯")
                        .reset());
    }

    /**
     * Helper class to print a generic elements objective card like this:
     * ╭─────────╮
     * │         │
     * │  2 FFF  │
     * │         │
     * ╰─────────╯
     * 
     * @param r        row of first character
     * @param c        column of first character
     * @param elements required elements
     * @param points   awarded points of the objective
     */
    private static void objectiveElementsPrinter(int r, int c, String[] elements, int points) {
        int left_padding;
        int righ_padding;

        int space_left = 5 - elements.length;

        if (space_left % 2 == 0) {
            left_padding = righ_padding = space_left / 2;
        } else {
            left_padding = space_left / 2;
            righ_padding = left_padding + 1;
        }

        String req = Arrays.stream(elements)
                .map(s -> "@|" + elementToColor(s).toString().toLowerCase() + " " + s + "|@")
                .collect(Collectors.joining());

        System.out.println(
                ansi()
                        .cursor(r, c)
                        .fg(YELLOW)
                        .a("╭─────────╮")
                        .cursor(r + 1, c)
                        .a("│         │")
                        .cursor(r + 2, c)
                        .a("│ ")
                        .fg(DEFAULT)
                        .a(" ".repeat(left_padding)).a(points + " ").render(req).a(" ".repeat(righ_padding))
                        .fg(YELLOW)
                        .a(" │")
                        .cursor(r + 3, c)
                        .a("│         │")
                        .cursor(r + 4, c)
                        .a("╰─────────╯")
                        .reset());
    }

    /**
     * This method is used to draw a card on screen at the given coordinates.
     * The cards will be printed using the following shape:
     * 
     * ╭─────────╮
     * │         │
     * │    4    │
     * │         │
     * ╰─────────╯
     * 
     * This method can be used to show possible placement positions.
     * 
     * @param r     row of first character
     * @param c     column of first character
     * @param index number to print in the middle of the card
     */
    public static void printBlank(int r, int c, int index) {
        int left_padding;
        int righ_padding;

        int space_left = 9 - Integer.toString(index).length();

        if (space_left % 2 == 0) {
            left_padding = righ_padding = space_left / 2;
        } else {
            left_padding = space_left / 2;
            righ_padding = left_padding + 1;
        }

        System.out.print(
                ansi()
                        .cursor(r, c)
                        .a("╭─────────╮")
                        .cursor(r + 1, c)
                        .a("│         │")
                        .cursor(r + 2, c)
                        .a("│" + " ".repeat(left_padding) + index + " ".repeat(righ_padding) + "│")
                        .cursor(r + 3, c)
                        .a("│         │")
                        .cursor(r + 4, c)
                        .a("╰─────────╯")
                        .reset());
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

    public static void init() {
        loadResourceCards();
        loadGoldCards();
        loadStarterCards();

        init = true;
    }

    private static void loadResourceCards() {
        resourceCards = new HashMap<>();
        Deck<ResourceCard> deck = ResourceDeckCreator.createDeck(observersList, atomicInt);

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

    private static void loadGoldCards() {
        goldCards = new HashMap<>();
        Deck<GoldCard> deck = GoldDeckCreator.createDeck(observersList, atomicInt);

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

    private static void loadStarterCards() {
        starterCards = new HashMap<>();
        Deck<StarterCard> deck = StarterDeckCreator.createDeck(observersList, atomicInt);

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

class LightResourceCard {
    final String type;
    final Map<CornerPosition, String> corners;
    final int points;

    public LightResourceCard(String type, Map<CornerPosition, String> corners, int points) {
        this.type = type;
        this.corners = corners;
        this.points = points;
    }
}

class LightGoldCard {
    final String type;
    final Map<CornerPosition, String> corners;
    final String points;
    final List<String> required;

    public LightGoldCard(String type, Map<CornerPosition, String> corners, String points, List<String> required) {
        this.type = type;
        this.corners = corners;
        this.points = points;
        this.required = required;
    }
}

class LightStarterCard {
    final List<String> resources;
    final Map<CornerPosition, String> cornersFront;
    final Map<CornerPosition, String> cornersBack;

    public LightStarterCard(List<String> resources, Map<CornerPosition, String> cornersFront,
            Map<CornerPosition, String> cornersBack) {
        this.resources = resources;
        this.cornersFront = cornersFront;
        this.cornersBack = cornersBack;
    }
}

class LightObjectiveCard {
    final int points;
    final String pattern;
    final String resources;

    public LightObjectiveCard(int points, String pattern, String resources) {
        this.points = points;
        this.pattern = pattern;
        this.resources = resources;
    }
}