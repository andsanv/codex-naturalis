package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.List;

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
}
