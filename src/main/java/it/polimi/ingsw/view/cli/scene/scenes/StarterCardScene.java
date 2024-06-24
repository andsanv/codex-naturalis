package it.polimi.ingsw.view.cli.scene.scenes;

import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class StarterCardScene extends Scene {
    public StarterCardScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
            new CLICommand("draw", "to draw your starter card", null),
            new CLICommand("front", "to play the front of the drawn card", null),
            new CLICommand("back", "to play the back of the drawn card", null)
        );
    }
}
