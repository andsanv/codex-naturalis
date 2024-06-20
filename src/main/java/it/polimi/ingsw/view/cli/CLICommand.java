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

    /**
     * Constructor of a command with no parameters.
     * 
     * @param name        name of the command
     * @param description description of the command
     */
    public CLICommand(String name, String description) {
        this.name = name;
        this.parameters = Optional.empty();
        this.description = description;
    }

    /**
     * Constructor of a command with parameters.
     * 
     * @param name        name of the command
     * @param description description of the command
     * @param parameters  parameters of the command as a list
     */
    public CLICommand(String name, List<String> parameters, String description) {
        this.name = name;
        this.parameters = Optional.of(parameters);
        this.description = description;
    }

    /**
     * Creates the ansi sequence to print the command.
     * 
     * @return the command as an Ansi object
     */
    public Ansi toAnsi() {
        return parameters.isPresent() ? ansi()
                .reset().a("+ ").fg(YELLOW)
                .a(name + parameters.stream()
                        .map(p -> " [" + p + "]")
                        .collect(Collectors.joining()))
                .reset().a(" " + description)
                : ansi().reset().a(" - ").fg(YELLOW).a(name).reset().a(" " + description);
    }
}
