package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawObjectiveCardsCommand;
import it.polimi.ingsw.distributed.commands.game.SelectObjectiveCardCommand;
import it.polimi.ingsw.distributed.events.game.EndedInitializationPhaseEvent;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICardUtils;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * Scene for objective card selection
 */
public class ObjectiveCardScene extends Scene {
    public ObjectiveCardScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("draw", "to draw your objective cards", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.secretObjectives.get() != null) {
                        CLIPrinter.displayError("You have already drawn your objective cards");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new DrawObjectiveCardsCommand(cli.token.get()));

                    CLIPrinter.displayLoadingMessage("Drawing card", cli.waitingGameEvent,
                            connectionHandler.isConnected, null);

                    if (cli.lastGameError.get() != null) {
                        CLIPrinter.displayError(cli.lastGameError.get());
                        cli.lastGameError.set(null);
                        return;
                    }

                    Ansi[][] starterCardAsAnsi = CLICardUtils.emptyAnsiMatrix(5, 23);
                    CLICardUtils.addCardToMatrix(starterCardAsAnsi,
                            CLICardUtils.cardToMatrix(cli.secretObjectives.get().first, CardSide.FRONT), 0, 0);
                    CLICardUtils.addCardToMatrix(starterCardAsAnsi,
                            CLICardUtils.cardToMatrix(cli.secretObjectives.get().second, CardSide.FRONT), 0, 12);
                    CLIPrinter.printAnsiGrid(starterCardAsAnsi);
                    System.out.println("   First      Second   ");

                }),
                new CLICommand("first", "to select the first objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.secretObjectives.get() == null)
                        CLIPrinter.displayError("Draw the objective cards first");

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new SelectObjectiveCardCommand(cli.token.get(), 0));
                    if (!CLIPrinter.displayLoadingMessage("Selecting objective",
                            cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                    System.out.println("Waiting for all players to pick the secret objective card");

                    try {
                        cli.objectivePhaseLatch.await();
                    } catch (InterruptedException e) {
                        sceneManager.stop();
                    }

                    if (sceneManager.getCurrentScene() != EndingGameInitScene.class)
                        sceneManager.transition(EndingGameInitScene.class);
                }),
                new CLICommand("second", "to select the second objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.secretObjectives.get() == null)
                        CLIPrinter.displayError("Draw the objective cards first");

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    cli.waitingGameEvent.set(true);
                    connectionHandler.sendToGameServer(new SelectObjectiveCardCommand(cli.token.get(), 1));
                    if (!CLIPrinter.displayLoadingMessage("Selecting objective",
                            cli.waitingGameEvent,
                            connectionHandler.isConnected, null)) {
                        sceneManager.transition(ConnectionLostScene.class);
                    }

                    System.out.println("Waiting for all players to pick the secret objective card");

                    try {
                        cli.objectivePhaseLatch.await();
                    } catch (InterruptedException e) {
                        sceneManager.stop();
                    }

                    if (sceneManager.getCurrentScene() != EndingGameInitScene.class)
                        sceneManager.transition(EndingGameInitScene.class);
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Secret objective card selection", YELLOW);

        System.out.println("Draw the secret objectives and decide which one you want to keep");
    }
}
