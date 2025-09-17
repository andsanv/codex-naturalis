package controller;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Class used to print formatted messages in the server.
 */
public class ServerPrinter {
    public static final boolean SHOW_ERROR = true;
    public static final boolean SHOW_WARNING = true;
    public static final boolean SHOW_INFO = true;
    public static final boolean SHOW_DEBUG = true;

    /**
     * Prints a formatted error.
     * 
     * @param error the error message
     */
    public static void displayError(String error) {
        if (SHOW_ERROR)
            System.out.println(ansi().reset().bg(RED).a("ERROR").reset().a(" " + error));
    }

    /**
     * Prints a formatted warning.
     * 
     * @param warning the warning message
     */
    public static void displayWarning(String warning) {
        if (SHOW_WARNING)
            System.out.println(ansi().reset().bg(YELLOW).a("WARNING").reset().a(" " + warning));
    }

    /**
     * Prints a formatted informational message.
     * 
     * @param info the info message
     */
    public static void displayInfo(String info) {
        if (SHOW_INFO)
            System.out.println(ansi().reset().bg(CYAN).a("INFO").reset().a(" " + info));
    }

    /**
     * Prints a formatted debug message.
     * 
     * @param debug the debug message
     */
    public static void displayDebug(String debug) {
        if (SHOW_DEBUG)
            System.out.println(ansi().reset().bg(WHITE).a("DEBUG").reset().a(" " + debug));
    }
}
