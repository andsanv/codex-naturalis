package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.ArrayList;

import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICardUtils;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.SceneManager;

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

        try {
            cli.endInitPhaseLatch.await();
        } catch (InterruptedException e) {
            CLIPrinter.displayError("Thread Interrupted");
        }

        if(sceneManager.getCurrentScene()!=GameScene.class)
            sceneManager.transition(GameScene.class);
    }

}
