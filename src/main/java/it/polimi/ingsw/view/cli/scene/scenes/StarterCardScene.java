package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

import java.util.Arrays;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawStarterCardCommand;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * Scene where the user can select the starter card.
 */
public class StarterCardScene extends Scene {
    private boolean cardDrawn;

    public StarterCardScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("draw", "to draw your starter card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (cardDrawn) {
                        CLIPrinter.displayError("You have already drawn your card");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new DrawStarterCardCommand(cli.token.get()));
                    if (!CLIPrinter.displayLoadingMessage("Drawing card", cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                }),
                new CLICommand("front", "to play the front of the drawn card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (!cardDrawn)
                        CLIPrinter.displayError("Draw a card first");



                }),
                new CLICommand("back", "to play the back of the drawn card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (!cardDrawn)
                        CLIPrinter.displayError("Draw a card first");

                }));
    }

    @Override
    public void onEntry() {
        cardDrawn = false;

        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Starter Card Selection", YELLOW);

        System.out.println("The final token assignments are:");
        System.out.println(ansi().fg(YELLOW));
        sceneManager.cli.getTokenToPlayerMap().entrySet().stream().forEach(e -> {
            System.out.println(e.getValue() + " -> " + e.getKey());
        });
        System.out.println(ansi().reset());

        System.out.println("You have to draw a starter card and decide how to place it on your board");
    }
}
