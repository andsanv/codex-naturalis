package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;

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
        System.out.println(ansi().reset().bg(color)
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
     * @param msg the available commands
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
     * It shows a loading message until the given condition becomes false.
     * 
     * @param msg       message of loading animation
     * @param isLoading an AtomicBoolean that, upon becoming false, stops the
     *                  loading animation
     */
    public static void displayLoadingMessage(String msg, AtomicBoolean isLoading) {
        String[] sequence = { "", ".", "..", "..." };
        int curr = 0;
        while (isLoading.get()) {
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
             * │ Lobby 13      │
             * ├───────────────┤
             * │ user#21       │
             * │ anotherUser#3 │
             * │ lastUser#1    │
             * ├───────────────┤
             * │ In-Game? X    │
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
}
