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
     * When being constructed, the scene saves a reference to the SceneManager
     * 
     * @param sceneManager the SceneManager that will use the scene
     */
    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Method called when entering the scene
     */
    abstract public void onEntry();

    /**
     * Method called when exiting the scene
     */
    abstract public void onExit();

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
     * @param sceneManager the sceneManager that is calling this method
     */
    abstract public void handleCommand(String[] args);
}
