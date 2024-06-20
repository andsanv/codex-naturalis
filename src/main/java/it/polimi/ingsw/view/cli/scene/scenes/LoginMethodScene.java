package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class LoginMethodScene extends Scene {

    public LoginMethodScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
            // new CLICommand("", commandsDescription);
        );
    }

    @Override
    public void onEntry() {
        CLIPrinter.displaySceneTitle("Login Menu", BLUE);
    }

    @Override
    public void handleCommand(String[] args) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommand'");
    }
    
}
