package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;
import java.util.List;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.PlayCardCommand;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.CLICardUtils;
import it.polimi.ingsw.view.cli.CLICommand;
import it.polimi.ingsw.view.cli.CLIPrinter;
import it.polimi.ingsw.view.cli.scene.Scene;
import it.polimi.ingsw.view.cli.scene.SceneManager;

public class GameScene extends Scene {

    public GameScene(SceneManager sceneManager) {
        super(sceneManager);

        this.commands = Arrays.asList(
                new CLICommand("play", Arrays.asList(""), "to play a card", () -> {
                    if (args.length != 3) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new PlayCardCommand(cli.token.get(), null, 0, null));
                }),
                new CLICommand("hand", "to show your hand", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    List<Integer> playerHand = cli.getPlayerHand();

                    Ansi[][] grid = CLICardUtils.emptyAnsiMatrix(5, 11 * 3 + 2);
                    CLICardUtils.addCardToMatrix(grid,
                            (playerHand.get(0) != null ? CLICardUtils.cardToMatrix(playerHand.get(0), CardSide.FRONT)
                                    : CLICardUtils.simpleCard(DEFAULT)),
                            0, 0);
                    CLICardUtils.addCardToMatrix(grid,
                            (playerHand.get(1) != null ? CLICardUtils.cardToMatrix(playerHand.get(1), CardSide.FRONT)
                                    : CLICardUtils.simpleCard(DEFAULT)),
                            0, 12);
                    CLICardUtils.addCardToMatrix(grid,
                            (playerHand.get(2) != null ? CLICardUtils.cardToMatrix(playerHand.get(2), CardSide.FRONT)
                                    : CLICardUtils.simpleCard(DEFAULT)),
                            0, 12);
                    CLIPrinter.printAnsiGrid(grid);
                }),
                new CLICommand("board", Arrays.asList("token"), "to show your board", () -> {
                    if (args.length != 3) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;
                    ConnectionHandler connectionHandler = cli.getConnectionHandler();

                    connectionHandler.sendToGameServer(new PlayCardCommand(cli.token.get(), null, 0, null));
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Playing", YELLOW);
    }

}
