package it.polimi.ingsw.view.cli.scene;

import java.util.List;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;

/**
 * Scenes of the CLI must extend this class.
 */
public abstract class Scene {
    /**
     * The SceneManager that is using the scene
     */
    protected SceneManager sceneManager;

    /**
     * A brief description that will be printed before listing the commands
     */
    protected String commandsDescription;

    /**
     * The list of available commands in the scene
     */
    protected List<CLICommand> commands;

    /**
     * CLI input trimmed and split.
     * It's needed so the CLICommands' lambdas can read it when handleCommand is
     * called.
     */
    protected String[] args;

    /**
     * When being constructed, the scene saves a reference to the SceneManager
     * 
     * @param sceneManager the SceneManager that will use the scene
     */
    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Method called when entering the scene
     * Unless overriden, calling it does nothing.
     */
    public void onEntry() {
    }

    /**
     * Method called when exiting the scene
     * Unless overriden, calling it does nothing.
     */
    public void onExit() {
    }

    /**
     * Lists the available commands for this scene
     */
    public final void availableCommands() {
        CLIPrinter.displayCommands(commandsDescription, commands);
    }

    /**
     * Perfoms an action depending on the given input
     * 
     * @param args         the user input parsed as a list of strings
     */
    public void handleCommand(String[] args) {
        this.args = args;

        commands.stream()
                .filter(c -> c.name.equalsIgnoreCase(args[0]))
                .findFirst()
                .ifPresentOrElse(CLICommand::execute,
                        () -> CLIPrinter.displayError("Invalid option"));
    }
}
