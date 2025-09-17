package view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.fusesource.jansi.Ansi;

import controller.usermanagement.LobbyInfo;
import controller.usermanagement.UserInfo;
import model.common.Elements;
import model.common.Items;
import model.common.Resources;

/**
 * This class provides static methods for writing formatted text
 */
public class CLIPrinter {
    /**
     * Prints the scene title with formatting.
     * The title can be at most 50 chars long.
     * 
     * @param title title to print
     */
    public static void displaySceneTitle(String title, Ansi.Color color) {
        int padding = 80 - title.length();
        System.out.println(ansi().reset().bg(color != null ? color : BLUE)
                .a(" ".repeat(Math.floorDiv(padding, 2)) + title + " ".repeat(Math.ceilDiv(padding, 2))).reset()
                .a("\n"));
    }

    /**
     * Prints an error message with formatting.
     * 
     * @param msg error message
     */
    public static void displayError(String msg) {
        System.out.println(ansi().reset().bg(RED).a("ERROR").reset().a(" " + msg));
    }

    /**
     * Prints all available commands.
     *
     * @param description the description
     * @param commands    the available commands
     */
    public static void displayCommands(String description, List<CLICommand> commands) {
        if (description != null)
            System.out.println(ansi().reset().a(description));

        commands.stream().map(CLICommand::toAnsi).forEach(System.out::println);
    }

    /**
     * This method clears the screen and repositions the cursor.
     */
    public static void clear() {
        System.out.println(ansi().reset().eraseScreen().cursor(0, 0));
    }

    /**
     * This method is called while waiting for a server event.
     * It shows a loading message until the given condition becomes false or the
     * client loses connection.
     * Can print an error message if given an AtomicReference of a String that is
     * checked after waiting.
     * 
     * @param msg         message of loading animation
     * @param isLoading   an AtomicBoolean that, upon becoming false, stops the
     *                    loading animation
     * @param isConnected an AtomicBoolean that is true if the client is connected
     *                    to the server
     * @param error       an error message that can occurr if the request was
     *                    invalid, set it to null to ignore
     * 
     * @return true if successfully waited for the server (even if an error message
     *         arrived), false if connection was lost
     */
    public static boolean displayLoadingMessage(String msg, AtomicBoolean isLoading, AtomicBoolean isConnected,
            AtomicReference<String> error) {
        String[] sequence = { "", ".", "..", "..." };
        int curr = 0;
        while (isLoading.get() && isConnected.get()) {
            System.out.print(ansi().reset().eraseLine().cursorToColumn(0).bg(GREEN).a("LOADING").reset()
                    .a(" " + msg + sequence[curr++ % sequence.length]));
            System.out.flush();

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
            }
        }

        System.out.println(ansi().reset().eraseLine().cursorToColumn(0));
        System.out.flush();

        // Early return if connection was lost
        if (!isConnected.get())
            return false;

        // Print the error if occurred
        if (error != null && error.get() != null) {
            displayError(error.get());
            error.set(null);
        }

        return true;
    }

    /**
     * Prints available lobbies.
     * If a UserInfo is given, the player will be highlighted.
     * 
     * @param lobbies  the list of lobbies to print
     * @param userInfo the UserInfo of the player to highlight
     */
    public static void printLobbies(List<LobbyInfo> lobbies, UserInfo userInfo) {
        if (lobbies == null || lobbies.isEmpty())
            System.out.println("There aren't any active lobbies");
        else
            /*
             * Lobbies will be printed with the following format
             * ╒═══════════════╕
             * │ Lobby 13 │
             * ├───────────────┤
             * │ user#21 │
             * │ anotherUser#3 │
             * │ lastUser#1 │
             * ├───────────────┤
             * │ In-Game? X │
             * ╘═══════════════╛
             */
            for (LobbyInfo lobby : lobbies) {
                final int length = Math.max(12,
                        lobby.users.stream().mapToInt(user -> user.toString().length() + 2).max().orElse(0));

                System.out.println("╒" + "═".repeat(length) + "╕");
                System.out.println(
                        "│ Lobby " + lobby.id + " ".repeat(length - 7 - Integer.toString(lobby.id).length()) + "│");
                System.out.println("├" + "─".repeat(length) + "┤");

                lobby.users.stream().forEach(user -> {
                    boolean inLobby = user.equals(userInfo);
                    if (user.equals(lobby.manager)) {
                        System.out.println(ansi().a("│ ").bg(WHITE).fg(inLobby ? CYAN : DEFAULT).a(user).reset()
                                .a(" ".repeat(length - 1 - user.toString().length()) + "│"));
                    } else {
                        System.out.println(ansi().a("│ ").fg(inLobby ? CYAN : DEFAULT).a(user).reset()
                                .a(" ".repeat(length - 1 - user.toString().length()) + "│"));
                    }
                });

                System.out.println("├" + "─".repeat(length) + "┤");
                System.out.println(
                        ansi().a("│ In-Game? ").fg(lobby.gameStarted ? GREEN : RED).a(lobby.gameStarted ? "V" : "X")
                                .reset()
                                .a(" ".repeat(length - 11) + "│"));
                System.out.println("╘" + "═".repeat(length) + "╛");
            }
    }

    /**
     * Method that, given a grid of Ansi sequences, prints it.
     * 
     * @param grid the grid of Ansi sequences
     */
    public static void printAnsiGrid(Ansi[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Prints the given player elements.
     * 
     * @param elements the player elements as a map from an Element to an Integer
     */
    public static void printPlayerElements(Map<Elements, Integer> elements) {
        System.out.println("Elements owned by the given player:");
        System.out.println(ansi().fg(CLICardUtils.elementToColor("P")).a("P").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Resources.PLANT)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("A")).a("A").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Resources.ANIMAL)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("F")).a("F").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Resources.FUNGI)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("I")).a("I").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Resources.INSECT)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("Q")).a("Q").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Items.QUILL)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("K")).a("K").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Items.INKWELL)).orElse(0)));
        System.out.println(ansi().fg(CLICardUtils.elementToColor("M")).a("M").reset().a(" -> ")
                .a(Optional.ofNullable(elements.get(Items.MANUSCRIPT)).orElse(0)));
    }
}
