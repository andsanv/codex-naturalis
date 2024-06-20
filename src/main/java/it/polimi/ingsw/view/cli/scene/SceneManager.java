package it.polimi.ingsw.view.cli.scene;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.jansi.AnsiConsole;

import it.polimi.ingsw.view.cli.CLI;

/**
 * The SceneManager is used for handling transitions between scenes of the CLI.
 */
public class SceneManager {
    /**
     * The scene that is currently active
     */
    private Scene currentScene = null;

    /**
     * To check if the Manager is currently running.
     * It's value is true at construction.
     */
    public final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Boolean that is true if the SceneManager has been started.
     */
    public boolean isStarted = false;

    /**
     * The CLI that uses the SceneManager.
     * It must be saved because the scenes need to call methods provided by the CLI.
     */
    public final CLI cli;

    /**
     * A map from the class to the constructed scene itself
     */
    public final Map<Class<? extends Scene>, Scene> scenes = new HashMap<>();

    /**
     * @param cli the CLI that is using the SceneManager
     */
    public SceneManager(CLI cli) {
        this.cli = cli;
    }

    /**
     * Registers a scene.
     * At most one scene for each class can exist at a time.
     * 
     * @param scene the scene to register
     */
    public void registerScene(Scene scene) {
        scenes.put(scene.getClass(), scene);
    }

    /**
     * Sets the initial scene.
     * Must be called before setting starting the SceneManager and returns early if
     * it has already been started.
     * 
     * @param initialScene the initial scene
     */
    public void setInitialScene(Class<? extends Scene> initialScene) {
        if (isStarted || isRunning.get())
            return;

        Scene scene = scenes.get(initialScene);

        if (scene != null)
            this.currentScene = scene;
        else
            throw new SceneNotRegisteredException(
                    "The class " + initialScene.getName() + " has no corresponding registered scene");
    }

    /**
     * Starts the SceneManager and installs the AnsiConsole.
     * This method can be called only once.
     * Returns early if no initial scene has been set or if the method has already
     * been called.
     */
    public void start() {
        if (isStarted || this.currentScene == null)
            return;

        isStarted = true;
        this.isRunning.set(true);

        AnsiConsole.systemInstall();

        currentScene.onEntry();
    }

    /**
     * Updates the current scene to the given one.
     * Calls onExit() of the old scene and onEntry() of new one.
     * 
     * @param scene the class of the scene to transition to
     */
    public void transition(Class<? extends Scene> scene) {
        currentScene.onExit();
        currentScene = scenes.get(scene);
        currentScene.onEntry();
    }

    /**
     * Stops the SceneManager, uninstalls the AnsiConsole and closes the game.
     * Returns early if the SceneManager isn't running.
     */
    public void stop() {
        if (!isRunning.get())
            return;

        isRunning.set(false);
        System.out.println(ansi().reset().a("Closing the game"));

        AnsiConsole.systemUninstall();
        System.exit(0);
    }

    /**
     * Handles the input provided by the user.
     * 
     * @param input the user input
     */
    public void handleInput(String input) {
        String[] args = input.trim().split("\\s+");

        if (args[0].equalsIgnoreCase("help")) {
            this.currentScene.availableCommands();
        } else if (args[0].equalsIgnoreCase("quit")) {
            this.isRunning.set(false);
        } else {
            this.currentScene.handleCommand(args);
        }
    }
}
