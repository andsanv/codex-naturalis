package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import model.player.PlayerToken;
import util.Trio;
import view.cli.CLICommand;
import view.cli.CLIPrinter;
import view.cli.scene.Scene;
import view.cli.scene.SceneManager;

/**
 * Scene with game results (final scoreboard)
 */
public class ResultsScene extends Scene {

    public ResultsScene(SceneManager sceneManager) {
        super(sceneManager);
        this.commands = Arrays.asList(
                new CLICommand("x", "to go back to the main menu", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid command");
                    sceneManager.transition(LobbiesScene.class);
                    // sceneManager.isRunning.set(false);
                    // System.exit(0);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Game Results", BLUE);
        System.out.println("Enter x to quit the game");
        System.out.println("\nThis is the final ranking:\n");
        int i = 1;
        for (Trio<PlayerToken, Integer, Integer> result : sceneManager.cli.gameResults.get()) {
            System.out.println(i + ": " + sceneManager.cli.getTokenToPlayerMap().get(result.first) + " as "
                    + result.first + " (" + result.second + " points)");
            i++;
        }
        System.out.println();
    }
}
