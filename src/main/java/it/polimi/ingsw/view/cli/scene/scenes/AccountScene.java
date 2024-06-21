package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.util.Arrays;

import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class AccountScene extends Scene {
    public AccountScene(SceneManager sceneManager) {
        super(sceneManager);

        // TODO 

        this.commandsDescription = "You can create an account or log in into yours";
        this.commands = Arrays.asList(
                new CLICommand("new", Arrays.asList("username"), "to create a new account", null),
                new CLICommand("login", Arrays.asList("username", "id"), "to use a previously created account", null));
    }

    @Override
    public void onEntry() {
        CLIPrinter.displaySceneTitle("Account Menu", BLUE);
    }

    @Override
    public void handleCommand(String[] args) {
        if(args[0].equalsIgnoreCase("new")) {
            if(args.length!=2) {
                CLIPrinter.displayError("There can't be spaces in the username");
                return;
            }
            // TODO stasera si riparte da qui
            // sceneManager.cli.getConnectionHandler().
        }
        else if(args[0].equalsIgnoreCase("login")) {

        }
        else {
            CLIPrinter.displayError("Unknown command");
        }
    }

}
