package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.jansi.Ansi;

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
     * @param condition an AtomicBoolean that, upon becoming false, stops the
     *                  loading animation
     */
    public static void displayLoadingMessage(String msg, AtomicBoolean waiting) {
        String[] sequence = { "", ".", "..", "..." };
        int curr = 0;
        while (waiting.get()) {
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
}
