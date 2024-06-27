package it.polimi.ingsw.view.cli.scene.scenes;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

import java.util.Arrays;
import java.util.List;

import org.fusesource.jansi.Ansi;

import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.distributed.commands.game.DrawGoldDeckCardCommand;
import it.polimi.ingsw.distributed.commands.game.DrawResourceDeckCardCommand;
import it.polimi.ingsw.distributed.commands.game.DrawVisibleGoldCardCommand;
import it.polimi.ingsw.distributed.commands.game.DrawVisibleResourceCardCommand;
import it.polimi.ingsw.distributed.commands.game.PlayCardCommand;
import it.polimi.ingsw.model.SlimGameModel;
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
                new CLICommand("play", Arrays.asList("card 1, 2 or 3 in hand", "placeholder", "f or b (side)"),
                        "to play a card", () -> {
                            if (args.length != 4) {
                                CLIPrinter.displayError("Too many arguments");
                                return;
                            }

                            CLI cli = sceneManager.cli;
                            ConnectionHandler connectionHandler = cli.getConnectionHandler();

                            if (!cli.canPlayCard.get()) {
                                CLIPrinter.displayError("You can't play a card now");
                                return;
                            }

                            int card;

                            try {
                                card = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                CLIPrinter.displayError("Invalid card");
                                return;
                            }

                            if (card < 1 || card > 3 || cli.getPlayerHand().get(card - 1) == null) {
                                CLIPrinter.displayError("Invalid card from hand");
                                return;
                            }

                            int cardId = cli.getPlayerHand().get(card - 1);

                            int placeholderPosition;

                            try {
                                placeholderPosition = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                CLIPrinter.displayError("Invalid position");
                                return;
                            }

                            if (cli.availablePositionsPlaceholders.get(placeholderPosition) == null) {
                                CLIPrinter.displayError("Invalid position");
                                return;
                            }

                            CardSide cardSide;

                            if (args[3].equals("f")) {
                                cardSide = CardSide.FRONT;
                            } else if (args[2].equals("b")) {
                                cardSide = CardSide.BACK;
                            } else {
                                CLIPrinter.displayError("Invalid card side");
                                return;
                            }

                            synchronized (cli.slimGameModelLock) {
                                if (!cli.slimGameModel.cardsPlayability
                                        .get(cli.token.get())
                                        .get(cardId)
                                        .stream()
                                        .filter(p -> p.first.equals(cardSide))
                                        .findAny()
                                        .map(p -> p.second)
                                        .orElseThrow()) {
                                    CLIPrinter.displayError("You can't play this side of the card here");
                                    return;
                                }
                            }

                            connectionHandler
                                    .sendToGameServer(new PlayCardCommand(cli.token.get(),
                                            cli.availablePositionsPlaceholders.get(placeholderPosition), cardId,
                                            cardSide));

                            cli.canDrawCard.set(true);
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
                    System.out.println("     1           2           3     ");
                }),
                new CLICommand("board", Arrays.asList("token"), "to show a player board", () -> {
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
                }),
                new CLICommand("objectives", "to show the common objective and you secret one", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    System.out.println("Common objectives:");
                    Ansi[][] grid = CLICardUtils.emptyAnsiMatrix(5, 23);
                    synchronized (cli.slimGameModelLock) {
                        CLICardUtils.addCardToMatrix(grid,
                                CLICardUtils.cardToMatrix(cli.slimGameModel.commonObjectives.get(0), CardSide.FRONT), 0,
                                0);
                        CLICardUtils.addCardToMatrix(grid,
                                CLICardUtils.cardToMatrix(cli.slimGameModel.commonObjectives.get(1), CardSide.FRONT), 0,
                                12);
                    }
                    CLIPrinter.printAnsiGrid(grid);
                    System.out.println("\nSecret objective:");
                    synchronized (cli.slimGameModelLock) {
                        CLIPrinter.printAnsiGrid(
                                CLICardUtils.cardToMatrix(cli.slimGameModel.tokenToSecretObjective.get(cli.token.get()),
                                        CardSide.FRONT));
                    }
                }),
                new CLICommand("scores", "to show the points of each player", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    System.out.println("The current scores are:");
                    synchronized (cli.slimGameModelLock) {
                        cli.slimGameModel.scores.entrySet().forEach(e -> {
                            System.out.println(e.getKey() + ": " + e.getValue());
                        });
                    }
                }),
                new CLICommand("drawable", "to show the decks and the drawable cards", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    Ansi[][] grid = CLICardUtils.emptyAnsiMatrix(18, 23);

                    synchronized (cli.slimGameModelLock) {
                        SlimGameModel slim = cli.slimGameModel;

                        CLICardUtils.addCardToMatrix(grid,
                                !slim.resourceDeck.isEmpty()
                                        ? CLICardUtils.cardToMatrix(slim.resourceDeck.getLast(), CardSide.BACK)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                0,
                                0);
                        CLICardUtils.addCardToMatrix(grid,
                                !slim.goldDeck.isEmpty()
                                        ? CLICardUtils.cardToMatrix(slim.goldDeck.getLast(), CardSide.BACK)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                0,
                                12);
                        CLICardUtils.addCardToMatrix(grid,
                                slim.visibleResourceCardsList.get(0) != null ? CLICardUtils
                                        .cardToMatrix(slim.visibleResourceCardsList.get(0), CardSide.FRONT)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                7,
                                0);
                        CLICardUtils.addCardToMatrix(grid,
                                slim.visibleResourceCardsList.get(1) != null ? CLICardUtils
                                        .cardToMatrix(slim.visibleResourceCardsList.get(1), CardSide.FRONT)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                7,
                                12);
                        CLICardUtils.addCardToMatrix(grid,
                                slim.visibleGoldCardsList.get(0) != null
                                        ? CLICardUtils.cardToMatrix(slim.visibleGoldCardsList.get(0), CardSide.FRONT)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                13,
                                0);
                        CLICardUtils.addCardToMatrix(grid,
                                slim.visibleGoldCardsList.get(1) != null
                                        ? CLICardUtils.cardToMatrix(slim.visibleGoldCardsList.get(1), CardSide.FRONT)
                                        : CLICardUtils.simpleCard(DEFAULT),
                                13,
                                12);
                    }
                    CLIPrinter.printAnsiGrid(grid);
                }),
                new CLICommand("drawres", "to draw a resource card from the deck", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    if (!cli.canDrawCard.get()) {
                        CLIPrinter.displayError("You can't draw a card now");
                        return;
                    }

                    SlimGameModel slim = cli.slimGameModel;

                    synchronized (cli.slimGameModelLock) {
                        if (slim.resourceDeck.isEmpty()) {
                            CLIPrinter.displayError("The resource deck is empty");
                            return;
                        }
                    }

                    cli.canDrawCard.set(false);

                    cli.getConnectionHandler().sendToGameServer(new DrawResourceDeckCardCommand(cli.token.get()));
                }),
                new CLICommand("drawgold", "to draw a gold card from the deck", () -> {
                    if (args.length != 1) {
                        CLIPrinter.displayError("Too many arguments");
                        return;
                    }

                    CLI cli = sceneManager.cli;

                    if (!cli.canDrawCard.get()) {
                        CLIPrinter.displayError("You can't draw a card now");
                        return;
                    }

                    SlimGameModel slim = cli.slimGameModel;

                    synchronized (cli.slimGameModelLock) {
                        if (slim.goldDeck.isEmpty()) {
                            CLIPrinter.displayError("The resource deck is empty");
                            return;
                        }
                    }

                    cli.canDrawCard.set(false);

                    cli.getConnectionHandler().sendToGameServer(new DrawGoldDeckCardCommand(cli.token.get()));
                }),
                new CLICommand("visiblegold", Arrays.asList("position (0 or 1)"),
                        "to draw a resource card from the visible cards", () -> {
                            if (args.length != 2) {
                                CLIPrinter.displayError("Too many arguments");
                                return;
                            }

                            CLI cli = sceneManager.cli;

                            if (!cli.canDrawCard.get()) {
                                CLIPrinter.displayError("You can't draw a card now");
                                return;
                            }

                            SlimGameModel slim = cli.slimGameModel;

                            int position;

                            try {
                                position = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                CLIPrinter.displayError("Invalid argument");
                                return;
                            }

                            if (position != 0 && position != 1) {
                                CLIPrinter.displayError("Invalid position of visible cards");
                                return;
                            }

                            synchronized (cli.slimGameModelLock) {
                                if (slim.visibleResourceCardsList.get(position) == null) {
                                    CLIPrinter.displayError("There is no card at that position");
                                    return;
                                }
                            }

                            cli.canDrawCard.set(false);

                            cli.getConnectionHandler()
                                    .sendToGameServer(new DrawVisibleResourceCardCommand(cli.token.get(), position));
                        }),
                new CLICommand("visiblegold", Arrays.asList("position (0 or 1)"),
                        "to draw a gold card from the visible cards",
                        () -> {
                            if (args.length != 2) {
                                CLIPrinter.displayError("Too many arguments");
                                return;
                            }

                            CLI cli = sceneManager.cli;

                            if (!cli.canDrawCard.get()) {
                                CLIPrinter.displayError("You can't draw a card now");
                                return;
                            }

                            SlimGameModel slim = cli.slimGameModel;

                            int position;

                            try {
                                position = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                CLIPrinter.displayError("Invalid argument");
                                return;
                            }

                            if (position != 0 && position != 1) {
                                CLIPrinter.displayError("Invalid position of visible cards");
                                return;
                            }

                            synchronized (cli.slimGameModelLock) {
                                if (slim.visibleGoldCardsList.get(position) == null) {
                                    CLIPrinter.displayError("There is no card at that position");
                                    return;
                                }
                            }

                            cli.canDrawCard.set(false);

                            cli.getConnectionHandler()
                                    .sendToGameServer(new DrawVisibleGoldCardCommand(cli.token.get(), position));

                        }));
    }

    @Override
    public void onEntry() {
        CLIPrinter.clear();
        CLIPrinter.displaySceneTitle("Playing", YELLOW);

        CLI cli = sceneManager.cli;
        System.out.println("You (" + cli.getUserInfo() + ") are playing as " + cli.token.get());
    }
}
