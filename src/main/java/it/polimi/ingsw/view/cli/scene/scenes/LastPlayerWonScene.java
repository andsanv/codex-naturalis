package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.MAGENTA;

import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

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
