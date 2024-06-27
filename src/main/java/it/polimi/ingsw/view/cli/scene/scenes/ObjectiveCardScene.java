package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawObjectiveCardsCommand;
import it.polimi.ingsw.distributed.commands.game.SelectObjectiveCardCommand;
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

                    if (cli.secretObjective.get() != -1) {
                        CLIPrinter.displayError("You have already selected your objective card");
                        return;
                    }

                    if (cli.secretObjectives.get() != null) {
                        CLIPrinter.displayError("You have already drawn your objective cards");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new DrawObjectiveCardsCommand(cli.token.get()));
                }),
                new CLICommand("first", "to select the first objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.secretObjective.get() != -1) {
                        CLIPrinter.displayError("You have already selected your objective card");
                        return;
                    }

                    if (cli.secretObjectives.get() == null) {
                        CLIPrinter.displayError("Draw the objective cards first");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new SelectObjectiveCardCommand(cli.token.get(), 0));

                    System.out.println("Waiting for all players to pick the secret objective card");
                }),
                new CLICommand("second", "to select the second objective card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.secretObjective.get() != -1) {
                        CLIPrinter.displayError("You have already selected your objective card");
                        return;
                    }

                    if (cli.secretObjectives.get() == null) {
                        CLIPrinter.displayError("Draw the objective cards first");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new SelectObjectiveCardCommand(cli.token.get(), 1));

                    System.out.println("Waiting for all players to pick the secret objective card");
                }),
                new CLICommand("show", "to show the drawn objective cards (or the selected one if you selected it)",
                        () -> {
                            if (args.length != 1)
                                CLIPrinter.displayError("Invalid option");

                            CLI cli = sceneManager.cli;

                            if (cli.secretObjectives.get() == null) {
                                CLIPrinter.displayError("Draw the objective cards first");
                                return;
                            }

                            if (cli.secretObjective.get() == -1) {
                                Ansi[][] objectiveCardsAsAnsi = CLICardUtils.emptyAnsiMatrix(5, 23);
                                CLICardUtils.addCardToMatrix(objectiveCardsAsAnsi,
                                        CLICardUtils.cardToMatrix(cli.secretObjectives.get().first, CardSide.FRONT), 0,
                                        0);
                                CLICardUtils.addCardToMatrix(objectiveCardsAsAnsi,
                                        CLICardUtils.cardToMatrix(cli.secretObjectives.get().second, CardSide.FRONT), 0,
                                        12);
                                CLIPrinter.printAnsiGrid(objectiveCardsAsAnsi);
                                System.out.println("   First      Second   ");
                            } else {
                                CLIPrinter.printAnsiGrid(
                                        CLICardUtils.cardToMatrix(cli.secretObjective.get(), CardSide.FRONT));
                                System.out.println("Selected objective card");
                            }
                        }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Secret objective card selection", YELLOW);

        System.out.println("Draw the secret objectives and decide which one you want to keep");
    }
}
