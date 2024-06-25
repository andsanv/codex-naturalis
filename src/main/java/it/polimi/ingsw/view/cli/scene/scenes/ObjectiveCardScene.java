package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawObjectiveCardsCommand;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class ObjectiveCardScene extends Scene {
    private boolean cardDrawn;

    public ObjectiveCardScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("draw", "to draw your objective cards", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (cardDrawn) {
                        CLIPrinter.displayError("You have already drawn your objective cards");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new DrawObjectiveCardsCommand(cli.token.get()));
                    if (!CLIPrinter.displayLoadingMessage("Drawing card", cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                    if (cli.lastGameError.get() != null) {
                        CLIPrinter.displayError(cli.lastGameError.get());
                        return;
                    }

                    cardDrawn = true;
                }),
                new CLICommand("first", "to select the first objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (!cardDrawn)
                        CLIPrinter.displayError("Draw the objective cards first");

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new DrawObjectiveCardsCommand(cli.token.get()));
                    if (!CLIPrinter.displayLoadingMessage("Drawing card", cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                    if (cli.lastGameError.get() != null) {
                        CLIPrinter.displayError(cli.lastGameError.get());
                        return;
                    }

                    cli.waitingGameEvent.set(true);
                    if (!CLIPrinter.displayLoadingMessage("Waiting for all players to pick the secret objective card",
                            cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }
                }),
                new CLICommand("second", "to select the second objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    if (!cardDrawn)
                        CLIPrinter.displayError("Draw the objective cards first");

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new DrawObjectiveCardsCommand(cli.token.get()));
                    if (!CLIPrinter.displayLoadingMessage("Drawing card", cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                    if (cli.lastGameError.get() != null) {
                        CLIPrinter.displayError(cli.lastGameError.get());
                        return;
                    }

                    cli.waitingGameEvent.set(true);
                    if (!CLIPrinter.displayLoadingMessage("Waiting for all players to pick the secret objective card",
                            cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }
                }));
    }

    @Override
    public void onEntry() {
        cardDrawn = false;

        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Secret objective card selection", YELLOW);

        System.out.println("Draw the secret objectives and decide which one you want to keep");
    }
}
