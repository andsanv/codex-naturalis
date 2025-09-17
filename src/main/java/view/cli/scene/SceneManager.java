package view.cli.scene;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.jansi.AnsiConsole;

import controller.usermanagement.UserInfo;
import distributed.commands.game.DirectMessageCommand;
import distributed.commands.game.GroupMessageCommand;
import view.cli.CLI;
import view.cli.CLIPrinter;

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
    public synchronized void registerScene(Scene scene) {
        scenes.put(scene.getClass(), scene);
    }

    /**
     * Sets the initial scene.
     * Must be called before setting starting the SceneManager and returns early if
     * it has already been started.
     * 
     * @param initialScene the initial scene
     */
    public synchronized void setInitialScene(Class<? extends Scene> initialScene) {
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
    public synchronized void start() {
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
    public synchronized void transition(Class<? extends Scene> scene) {
        currentScene.onExit();
        currentScene = scenes.get(scene);
        currentScene.onEntry();
    }

    /**
     * Stops the SceneManager, uninstalls the AnsiConsole and closes the game.
     * Returns early if the SceneManager isn't running.
     */
    public synchronized void stop() {
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
    public synchronized void handleInput(String input) {
        String[] args = input.trim().split("\\s+");

        if (args[0].equalsIgnoreCase("help")) {
            this.currentScene.availableCommands();
        } else if (args[0].equalsIgnoreCase("quit")) {
            this.isRunning.set(false);
            System.exit(0);
        } else if (cli.inGame.get() && args[0].equalsIgnoreCase("dm")) {
            // Handle direct message

            if (args.length < 3) {
                CLIPrinter.displayError("Invalid formatting for send direct message command");
                return;
            }

            UserInfo receiver;

            try {
                receiver = UserInfo.fromString(args[1]);
            } catch (IllegalArgumentException e) {
                CLIPrinter.displayError("Invalid user");
                return;
            }

            if (cli.getUserInfo().equals(receiver)) {
                CLIPrinter.displayError("Can't send a message to yourself");
                return;
            }

            if (!cli.usersInGame.get().contains(receiver)) {
                CLIPrinter.displayError("No user in game matches the given receiver");
                return;
            }

            cli.getConnectionHandler().sendToGameServer(new DirectMessageCommand(cli.getUserInfo(), receiver,
                    String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
        } else if (cli.inGame.get() && args[0].equalsIgnoreCase("gm")) {
            // Handle group message

            if (args.length < 2) {
                CLIPrinter.displayError("Invalid formatting for send group message command");
                return;
            }

            cli.getConnectionHandler().sendToGameServer(new GroupMessageCommand(cli.getUserInfo(),
                    String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
        } else if (cli.inGame.get() && args[0].equalsIgnoreCase("printgm")) {
            if (args.length != 1) {
                CLIPrinter.displayError("Too many arguments");
                return;
            }

            System.out.println("Group messages:");
            cli.getGroupMessages().stream().forEach(p -> System.out.println(p.first + ": " + p.second));
        } else if (cli.inGame.get() && args[0].equalsIgnoreCase("printdm")) {
            if (args.length != 2) {
                CLIPrinter.displayError("Invalid arguments");
                return;
            }

            UserInfo other;

            try {
                other = UserInfo.fromString(args[1]);
            } catch (IllegalArgumentException e) {
                CLIPrinter.displayError("Invalid user");
                return;
            }

            if (cli.getUserInfo().equals(other)) {
                CLIPrinter.displayError("Can't see messages with yourself");
                return;
            }

            if (!cli.usersInGame.get().contains(other)) {
                CLIPrinter.displayError("No user in game matches the given user");
                return;
            }

            System.out.println("Messages with " + other);
            cli.getDirectMessages(other).stream().forEach(p -> System.out.println(p.first + ": " + p.second));
        } else {
            this.currentScene.handleCommand(args);
        }
    }

    /**
     * Current scene as Class getter.
     * 
     * @return the Class of the current scene
     */
    public synchronized Class<? extends Scene> getCurrentScene() {
        return currentScene.getClass();
    }
}
