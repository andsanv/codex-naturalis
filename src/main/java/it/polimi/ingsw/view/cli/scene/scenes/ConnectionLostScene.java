package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.RED;

import java.util.Arrays;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * The user is in this scene when he loses the connection to the server.
 */
public class ConnectionLostScene extends Scene {

    public ConnectionLostScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("reconnect", "to attempt restoring the connection to the server", () -> {
                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    if (connectionHandler.reconnect()) {
                        System.out.println("Successfully reconnected");

                        if (cli.inGame.get()) {
                            sceneManager.transition(GameScene.class);
                        } else
                            sceneManager.transition(LobbiesScene.class);
                    } else {
                        CLIPrinter.displayError("Reconnection failed");
                    }
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Connection Lost", RED);
        System.out.println("The connection to the Codex Naturalis server was lost");
    }
}
