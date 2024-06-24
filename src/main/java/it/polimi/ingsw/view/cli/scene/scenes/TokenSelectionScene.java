package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class TokenSelectionScene extends Scene {

    public TokenSelectionScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("blue", commandsDescription, null),
                new CLICommand("green", commandsDescription, null),
                new CLICommand("red", commandsDescription, null),
                new CLICommand("yellow", commandsDescription, null)
        );
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Match Started - Token Selection", YELLOW);

        System.out.println("Welcome to the match! You are playing with:");
        System.out.println(ansi().fg(YELLOW));
        sceneManager.cli.usersInGame.get().stream().forEach(System.out::println);
        System.out.println(ansi().reset());

        System.out.println("You have to pick a token to play:");
    }
}
