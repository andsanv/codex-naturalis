package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;
import java.util.List;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.PlayCardCommand;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.player.PlayerToken;
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
                new CLICommand("play", Arrays.asList("card 1, 2 or 3 in hand", "position", "f or b (side)"),
                        "to play a card", () -> {
                            if (args.length != 4) {
                                CLIPrinter.displayError("Too many arguments");
                                return;
                            }

                            CLI cli = sceneManager.cli;
                            ConnectionHandler connectionHandler = cli.getConnectionHandler();

                            int card;

                            try {
                                card = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                CLIPrinter.displayError("Invalid card");
                                return;
                            }

                            if (card < 1 || card > 3) {
                                CLIPrinter.displayError("Invalid card from hand");
                                return;
                            }

                            int cardId = cli.getPlayerHand().get(card - 1);

                            CardSide cardSide;

                            if (args[2].equals("f")) {
                                cardSide = CardSide.FRONT;
                            } else if (args[2].equals("b")) {
                                cardSide = CardSide.BACK;
                            } else {
                                CLIPrinter.displayError("Invalid card side");
                                return;
                            }

                            connectionHandler
                                    .sendToGameServer(new PlayCardCommand(cli.token.get(), null, cardId, cardSide));
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
                new CLICommand("board", Arrays.asList("token"), "to show a board", () -> {
                    if (args.length != 2) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    if (cli.getTokenToPlayerMap().keySet().stream().map(t -> t.toString())
                            .noneMatch(t -> t.equalsIgnoreCase(args[1]))) {
                        CLIPrinter.displayError("Invalid token");
                        return;
                    }

                    PlayerToken token = PlayerToken.valueOf(args[1].toUpperCase());

                    if (token.equals(cli.token.get())) {
                        Ansi[][] grid;
                        synchronized (cli.availablePositionsPlaceholders) {
                            if (cli.availablePositionsPlaceholders.isEmpty()) {
                                grid = CLICardUtils.createBoard(cli.getBoard(cli.token.get()));
                            } else {
                                grid = CLICardUtils.createBoardWithPlayability(cli.getBoard(cli.token.get()),
                                        cli.availablePositionsPlaceholders);
                            }
                        }
                        CLIPrinter.printAnsiGrid(grid);
                    } else {
                        Ansi[][] grid;
                        grid = CLICardUtils.createBoard(cli.getBoard(token));
                        CLIPrinter.printAnsiGrid(grid);
                    }
                }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Playing", YELLOW);
    }

}
