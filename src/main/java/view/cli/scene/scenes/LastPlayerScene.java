package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.MAGENTA;

import java.util.ArrayList;

import view.cli.CLIPrinter;
import view.cli.scene.Scene;
import view.cli.scene.SceneManager;

/**
 * Scene where the player is the last one connected
 */
public class LastPlayerScene extends Scene {

    public LastPlayerScene(SceneManager sceneManager) {
        super(sceneManager);
        this.commands = new ArrayList<>();
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("You are the last player connected", MAGENTA);
        System.out.println("Waiting for players to reconnect, if they don't you will be declared the winner.");
    }

}
