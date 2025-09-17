package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.MAGENTA;

import java.util.Arrays;

import view.cli.CLICommand;
import view.cli.CLIPrinter;
import view.cli.scene.Scene;
import view.cli.scene.SceneManager;

/**
 * Scene where the player was the last one connected and won after the timer ended
 */
public class LastPlayerWonScene extends Scene {

    public LastPlayerWonScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("x", "to go back to the main menu", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid command");

                    sceneManager.transition(LobbiesScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("You won", MAGENTA);
        System.out.println("You have been declared winner because everyone else failed to reconnect.");
        System.out.println("\nEnter x to go back to the main menu");
    }
}
