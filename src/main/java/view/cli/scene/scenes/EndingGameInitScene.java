package view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.ArrayList;

import model.card.CardSide;
import view.cli.CLI;
import view.cli.CLICardUtils;
import view.cli.CLIPrinter;
import view.cli.scene.SceneManager;

/**
 * Scene where the game is about to start with player turns (after setup phase)
 */
public class EndingGameInitScene extends GameScene {

    public EndingGameInitScene(SceneManager sceneManager) {
        super(sceneManager);
        this.commands = new ArrayList<>();
    }

    @Override
    public void onEntry() {
        CLI cli = sceneManager.cli;

        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Initializing the game", YELLOW);
        System.out.println("\nStarter card:");
        CLIPrinter.printAnsiGrid(CLICardUtils.cardToMatrix(cli.starterCard.get().first, cli.starterCard.get().second));
        System.out.println("\nSecret objective");
        CLIPrinter.printAnsiGrid(CLICardUtils.cardToMatrix(cli.secretObjectives.get().first, CardSide.FRONT));
    }
}
