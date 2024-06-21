package it.polimi.ingsw.view.cli;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;

/**
 * A CLICommand represents one of the possible command options available to the
 * user in the CLI.
 */
public class CLICommand {
    public final String name;
    public final Optional<List<String>> parameters;
    public final String description;
    private final Runnable action;

    /**
     * Constructor of a command with no parameters.
     * 
     * @param name        name of the command
     * @param description description of the command
     * @param action      the action to perform when execute is called
     */
    public CLICommand(String name, String description, Runnable action) {
        this.name = name;
        this.description = description;
        this.action = action;
        this.parameters = Optional.empty();
    }

    /**
     * Constructor of a command with parameters.
     * 
     * @param name        name of the command
     * @param description description of the command
     * @param parameters  parameters of the command as a list
     * @param action      the action to perform when execute is called
     */
    public CLICommand(String name, List<String> parameters, String description, Runnable action) {
        this.name = name;
        this.parameters = Optional.of(parameters);
        this.description = description;
        this.action = action;
    }

    /**
     * Creates the ansi sequence to print the command.
     * 
     * @return the command as an Ansi object
     */
    public Ansi toAnsi() {
        return parameters.isPresent() ? ansi()
                .reset().a("+ ").fg(YELLOW)
                .a(name + " " + parameters.get().stream()
                        .map(p -> "<" + p + "> ")
                        .collect(Collectors.joining()))
                .reset().a(description)
                : ansi().reset().a(" - ").fg(YELLOW).a(name).reset().a(" " + description);
    }

    /**
     * If there is an action, calling this method runs the action.
     */
    public void execute() {
        if (action != null) {
            action.run();
        }
    }
}
