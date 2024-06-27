package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

import java.util.Arrays;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawStarterCardCommand;
import it.polimi.ingsw.distributed.commands.game.SelectStarterCardSideCommand;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.CLICardUtils;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

/**
 * Scene where the user can select the starter card.
 */
public class StarterCardScene extends Scene {
    public StarterCardScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("draw", "to draw your starter card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.starterCard.get() != null) {
                        CLIPrinter.displayError("You have already drawn your card");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new DrawStarterCardCommand(cli.token.get()));
                }),
                new CLICommand("front", "to play the front of the drawn card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.starterCard.get() == null) {
                        CLIPrinter.displayError("Draw a card first");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler
                            .sendToGameServer(new SelectStarterCardSideCommand(cli.token.get(), CardSide.FRONT));

                    System.out.println("Waiting for all users to draw and choose the side of their starter card");
                }),
                new CLICommand("back", "to play the back of the drawn card", () -> {
                    if (args.length != 1)
                        CLIPrinter.displayError("Invalid option");

                    CLI cli = sceneManager.cli;

                    if (cli.starterCard.get() == null) {
                        CLIPrinter.displayError("Draw a card first");
                        return;
                    }

                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler
                            .sendToGameServer(new SelectStarterCardSideCommand(cli.token.get(), CardSide.BACK));

                    System.out.println("Waiting for all users to draw and choose the side of their starter card");
                }),
                new CLICommand("show", "to show the drawn starter card",
                        () -> {
                            if (args.length != 1)
                                CLIPrinter.displayError("Invalid option");

                            CLI cli = sceneManager.cli;

                            if (cli.starterCard.get() == null) {
                                CLIPrinter.displayError("Draw the objective cards first");
                                return;
                            }

                            Ansi[][] starterCardAsAnsi = CLICardUtils.emptyAnsiMatrix(5, 23);
                            CLICardUtils.addCardToMatrix(starterCardAsAnsi,
                                    CLICardUtils.cardToMatrix(cli.starterCard.get().first, CardSide.FRONT), 0, 0);
                            CLICardUtils.addCardToMatrix(starterCardAsAnsi,
                                    CLICardUtils.cardToMatrix(cli.starterCard.get().first, CardSide.BACK), 0, 12);
                            CLIPrinter.printAnsiGrid(starterCardAsAnsi);
                            System.out.println("   Front       Back    ");

                        }));
    }

    @Override
    public void onEntry() {
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
