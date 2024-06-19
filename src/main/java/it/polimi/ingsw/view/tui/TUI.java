package it.polimi.ingsw.view.tui;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import it.polimi.ingsw.model.common.Resources;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.UserInfo;
import it.polimi.ingsw.distributed.commands.main.ConnectionCommand;
import it.polimi.ingsw.distributed.commands.main.CreateLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.JoinLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.StartGameCommand;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.connection.ConnectionHandler;
import it.polimi.ingsw.view.connection.RMIConnectionHandler;
import it.polimi.ingsw.view.connection.SocketConnectionHandler;

public class TUI implements UI {
    private static String CODEX_NATURALIS = " _____           _             _   _       _                   _ _     \n"
            + //
            "/  __ \\         | |           | \\ | |     | |                 | (_)    \n" + //
            "| /  \\/ ___   __| | _____  __ |  \\| | __ _| |_ _   _ _ __ __ _| |_ ___ \n" + //
            "| |    / _ \\ / _` |/ _ \\ \\/ / | . ` |/ _` | __| | | | '__/ _` | | / __|\n" + //
            "| \\__/\\ (_) | (_| |  __/>  <  | |\\  | (_| | |_| |_| | | | (_| | | \\__ \\\n" + //
            " \\____/\\___/ \\__,_|\\___/_/\\_\\ \\_| \\_/\\__,_|\\__|\\__,_|_|  \\__,_|_|_|___/\n" + //
            "                                                                       \n" + //
            "                                                                       ";

    public static void main(String[] args) {
        new TUI().start();
    }

    private UserInfo userInfo = null;
    private Object userInfoLock = new Object();

    private Scanner input = new Scanner(System.in);
    private ConnectionHandler connectionHandler = null;
    private State state = State.HOME;
    private boolean running = true;

    private List<LobbyInfo> availableLobbies = null;
    private Object availableLobbiesLock = new Object();

    private AtomicBoolean waitingForLobbyCreation = new AtomicBoolean(false);
    private AtomicBoolean waitingUserInfo = new AtomicBoolean(false);
    private AtomicBoolean waitingForLobbyJoining = new AtomicBoolean(false);
    private AtomicBoolean waitingStartGame = new AtomicBoolean(false);

    private AtomicInteger currentLobbyid = new AtomicInteger(-1);

    private ExecutorService helperThread = Executors.newSingleThreadExecutor();

    public TUI() {
    }

    public void start() {
        new Thread(this::userHandler).start();
    }

    private void userHandler() {
        AnsiConsole.systemInstall();

        System.out.println(ansi().eraseScreen().cursor(0, 0));

        System.out.println(ansi().a(CODEX_NATURALIS).reset().a("\n"));

        while (running) {
            switch (state) {
                case HOME:
                    displayScreenTitle("HOME", CYAN);
                    homeScreen();
                    break;
                case LOBBY:
                    displayScreenTitle("LOBBIES", CYAN);
                    lobbyScreen();
                    break;
                case RECONNECTION:
                    displayScreenTitle("OFFLINE", RED);
                case END:
                default:
                    running = false;
                    break;
            }
        }

        // TODO end here or in prompt() (decide between moving to END state or exit(0))
        AnsiConsole.systemUninstall();
    }

    /**
     * Attempts to read UserInfo from file.
     * The saved UserInfo is the last one used on the same machine in the same
     * location.
     * 
     * @return true if found, false if not found or if an error occurred
     */
    private boolean retrieveUserInfo() {
        // Check if a backup exists, open the stream and try to deserialize it
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("userInfo.ser"));
            userInfo = (UserInfo) in.readObject();
            in.close();
            return true;
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            return false;
        }
    }

    /**
     * Attempts to save UserInfo to file, to use it for future logins.
     */
    private void saveUserInfo() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("userInfo.ser"));
            out.writeObject(userInfo);
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * Prints the prompt and reads user input.
     * 
     * @return the user's input
     */
    private String prompt() {
        System.out.print(ansi().fg(BLUE).a("> ").reset());
        System.out.flush();

        String in = input.nextLine();

        // System.out.println();

        if (in.equalsIgnoreCase("exit")) {
            System.out.println();
            quit();
        }

        return in;
    }

    /**
     * This function closes the game.
     */
    private void quit() {
        System.out.println(ansi().a("Closing the game...").reset());
        AnsiConsole.systemUninstall();
        System.exit(0);
    }

    /**
     * Prints an error message with formatting.
     * 
     * @param msg error message
     */
    private void displayError(String msg) {
        System.out.println(ansi().fg(RED).a("ERROR: ").reset().a(msg));
    }

    /**
     * Prints a screen title with formatting.
     * The title can be at most 50 chars long.
     * 
     * @param title title to print
     */
    private void displayScreenTitle(String title, Ansi.Color color) {
        int padding = 71 - title.length();
        System.out.println(ansi().bg(color)
                .a(" ".repeat(Math.floorDiv(padding, 2)) + title + " ".repeat(Math.ceilDiv(padding, 2))).reset()
                .a("\n"));
    }

    /**
     * This function is called while waiting for a server event.
     * It shows a loading message until the given condition becomes false.
     * The condition is set to true before returning.
     * 
     * @param msg       message of loading animation
     * @param condition an AtomicBoolean that stops the loading animation when set
     *                  to false
     */
    private void displayLoadingMessage(String msg, AtomicBoolean condition) {
        String[] sequence = { "", ".", "..", "..." };
        int curr = 0;
        while (condition.get()) {
            System.out.print(ansi().eraseLine().cursorToColumn(0).bg(GREEN).a("LOADING").reset()
                    .a(" " + msg + sequence[curr++ % sequence.length]));
            System.out.flush();

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("");
        condition.set(true);
    }

    /**
     * In the home screen, the user can choose wether to use a previously created
     * account or a new one and the connection type (Socket or RMI).
     * If he isn't in a game, he will see the lobby screen, otherwise he will
     * reconnect to the game.
     */
    private void homeScreen() {
        System.out
                .println(ansi().a("Do you want to connect with Socket or RMI?\n(enter ").fg(YELLOW).a("socket").reset()
                        .a(" for a Socket connection, ").fg(YELLOW).a("rmi").reset().a(" to use an RMI connection)"));

        while (true) {
            String connectionType = prompt();

            if (connectionType.equalsIgnoreCase("socket")) {
                try {
                    connectionHandler = new SocketConnectionHandler(this);
                } catch (Exception e) {
                    displayError(
                            "The server is currently unavailable, couldn't connect through sockets, please try later.\n");
                    quit();
                }

                break;
            } else if (connectionType.equalsIgnoreCase("rmi")) {
                try {
                    connectionHandler = new RMIConnectionHandler(this);
                } catch (Exception e) {
                    displayError(
                            "The server is currently unavailable, couldn't connect through RMI, please try later.\n");
                    quit();
                }
                break;
            } else {
                displayError("Invalid connection type, choose another one. ");
            }
        }

        // TODO check if connection is on or off

        boolean askForUsername = true;

        // Check if the user already has an account
        if (retrieveUserInfo()) {
            System.out.println(ansi()
                    .a("\nThe user ").fg(CYAN).a(userInfo).reset()
                    .a(" has been found, do you want to continue with this account?\n(enter ")
                    .fg(YELLOW).a("yes").reset().a(" to continue, ").fg(YELLOW).a("no").reset()
                    .a(" to create a new account)"));

            while (true) {
                String command = prompt();

                if (command.equalsIgnoreCase("yes")) {
                    waitingUserInfo.set(true);

                    // Attempt connection to the server
                    connectionHandler.reconnect();
                    displayLoadingMessage("Logging in", waitingUserInfo);

                    askForUsername = false;
                    break;
                } else if (command.equalsIgnoreCase("no")) {
                    break;
                } else {
                    displayError("Invalid choice, please try again.");
                }
            }
        }

        // Enter this loop if the user wants a new account
        if (askForUsername) {
            System.out.println("\nChoose an username:");

            while (true) {
                String username = prompt();

                if (username.length() < 3) {
                    displayError("The username lenght must be at least three characters long");
                    continue;
                }

                waitingUserInfo.set(true);
                connectionHandler.sendToMainServer(new ConnectionCommand(username));
                break;
            }

            displayLoadingMessage("Creating an account", waitingUserInfo);
        }

        // TODO check if the client is already connected to a game (handle reconnection)

        state = State.LOBBY;
    }

    /**
     * The user enters the reconnection screen when he loses connection to the
     * server, both in game and in the main menu.
     */
    private void reconnectionScreen() {
        AtomicBoolean waitingReconnectionResult = new AtomicBoolean(true);
        AtomicBoolean reconnected = new AtomicBoolean(false);

        System.out.println(ansi().a("Connection to the server lost\n")
                .fg(YELLOW).a("  exit").reset().a(" to close the game\n")
                .fg(YELLOW).a("  enter").reset().a(" (or anything else) to attempt reconnection"));

        while (true) {
            prompt();

            helperThread.submit(() -> {
                reconnected.set(this.connectionHandler.reconnect());
            });
            displayLoadingMessage("Reconnecting", waitingReconnectionResult);

            if (reconnected.get()) {
                System.out.println(ansi().fg(GREEN).a("Successfully reconnected"));

                break;
            } else {
                System.out.println(ansi().fg(RED).a("Reconnection failed, please try again"));
            }
        }
    }

    /**
     * In the lobby screen the user can join, create, see and quit lobbies.
     */
    private void lobbyScreen() {
        System.out.println(ansi().a("You can do the following actions:"));
        System.out.println(ansi().fg(YELLOW).a("  list").reset().a(" available lobbies"));
        System.out.println(
                ansi().fg(YELLOW).a("  join [").reset().a("id").fg(YELLOW).a("]").reset().a(" an existing lobby"));
        System.out.println(ansi().fg(YELLOW).a("  create").reset().a(" a new lobby"));
        System.out.println(ansi().fg(YELLOW).a("  start").reset().a(" a game (you must be the lobby leader)\n"));

        while (true) {
            String[] command = prompt().split("\\s+");

            if (command[0].equalsIgnoreCase("list")) {
                System.out.println();
                printLobbies();
            } else if (command[0].equalsIgnoreCase("join")) {
                int lobbyId;

                try {
                    lobbyId = Integer.valueOf(command[1]);
                } catch (Exception e) {
                    displayError("Invalid lobby number");
                    continue;
                }

                waitingForLobbyJoining.set(true);
                connectionHandler.sendToMainServer(new JoinLobbyCommand(userInfo, lobbyId));
                displayLoadingMessage("Joining the lobby", waitingForLobbyJoining);

                System.out.println("\nLobby joined!\n");

                printLobbies();
            } else if (command[0].equalsIgnoreCase("create")) {
                waitingForLobbyCreation.set(true);
                connectionHandler.sendToMainServer(new CreateLobbyCommand(userInfo));
                displayLoadingMessage("Waiting for lobby creation", waitingForLobbyCreation);

                System.out.println("\nYour lobby has been created!\n");
                printLobbies();
            } else if (command[0].equalsIgnoreCase("start")) {
                waitingForLobbyJoining.set(true);

                if (currentLobbyid.get() != -1) {
                    connectionHandler.sendToMainServer(new StartGameCommand(userInfo, currentLobbyid.get()));
                    displayLoadingMessage("Starting the game", waitingForLobbyJoining);
                } else {
                    displayError("You are not in a lobby");
                }
            } else {
                displayError("Invalid option");
            }
        }

    }

    /**
     * Prints available lobbies using the following format
     * 
     * ╒═══════════════╕
     * │ Lobby 13 │
     * ├───────────────┤
     * │ user#21 │
     * │ anotherUser#3 │
     * │ lastUser#1 │
     * ├───────────────┤
     * │ In-Game? X │
     * ╘═══════════════╛
     */
    private void printLobbies() {
        synchronized (availableLobbiesLock) {
            if (this.availableLobbies == null || this.availableLobbies.isEmpty())
                System.out.println("No lobbies available\n");
            else
                for (LobbyInfo lobby : availableLobbies) {
                    final int length = Math.max(12,
                            lobby.users.stream().mapToInt(user -> user.toString().length() + 2).max().orElse(0));

                    System.out.println("╒" + "═".repeat(length) + "╕");
                    System.out.println(
                            "│ Lobby " + lobby.id + " ".repeat(length - 7 - Integer.toString(lobby.id).length()) + "│");
                    System.out.println("├" + "─".repeat(length) + "┤");

                    lobby.users.stream().forEach(user -> {
                        boolean inLobby = user.equals(userInfo);
                        if (user.equals(lobby.manager)) {
                            System.out.println(ansi().a("│ ").bg(WHITE).fg(inLobby ? CYAN : DEFAULT).a(user).reset()
                                    .a(" ".repeat(length - 1 - user.toString().length()) + "│"));
                        } else {
                            System.out.println(ansi().a("│ ").fg(inLobby ? CYAN : DEFAULT).a(user).reset()
                                    .a(" ".repeat(length - 1 - user.toString().length()) + "│"));
                        }
                    });

                    System.out.println("├" + "─".repeat(length) + "┤");
                    System.out.println(
                            ansi().a("│ In-Game? ").fg(lobby.gameStarted ? GREEN : RED).a(lobby.gameStarted ? "V" : "X")
                                    .reset()
                                    .a(" ".repeat(length - 11) + "│"));
                    System.out.println("╘" + "═".repeat(length) + "╛");
                }
        }
    }

    @Override
    public void handleUserInfo(UserInfo userInfo) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
            waitingUserInfo.set(false);
            saveUserInfo();
        }
    }

    @Override
    public void handleServerError(String error) {
        System.err.println(error);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleServerError'");
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        synchronized (availableLobbiesLock) {
            this.availableLobbies = lobbies;

            // Check if the user is in a lobby
            boolean found = false;
            for (LobbyInfo lobby : lobbies) {
                if (lobby.users.contains(userInfo)) {
                    currentLobbyid.set(lobby.id);
                    waitingForLobbyJoining.set(false);
                    found = true;
                    break;
                }
            }
            if (!found)
                currentLobbyid.set(-1);

            // Check if a lobby with the current user as manager exists
            if (lobbies.stream().map(lobby -> lobby.manager).anyMatch(manager -> manager.equals(userInfo)))
                waitingForLobbyCreation.set(false);
        }
    }

    @Override
    public void handleReceivedConnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleReceivedConnection'");
    }

    @Override
    public void handleScoreTrackEvent(PlayerToken senderToken, int score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleScoreTrackEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int playedCardId, CardSide playedCardSide,
            Coords playedCardCoordinates) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex) {

    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Optional<Integer> replacementCardId, boolean deckEmptied, Optional<Resources> nextCardSeed, int handIndex) {

    }

    @Override
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnStarterCardEvent'");
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleChosenStarterCardSideEvent'");
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedStarterCardPhaseEvent'");
    }

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int firstDrawnCardId, int secondDrawnCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnObjectiveCardsEvent'");
    }

    @Override
    public void handleChosenObjectiveCardEvent(PlayerToken playerToken, int chosenCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleChosenObjectiveCardEvent'");
    }

    @Override
    public void handleEndedObjectiveCardPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedObjectiveCardPhaseEvent'");
    }

    @Override
    public void handleCommonObjectiveEvent(int firstCommonObjectiveId, int secondCommonObjectiveId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCommonObjectiveEvent'");
    }

    @Override
    public void handleDirectMessageEvent(PlayerToken senderToken, PlayerToken receiverToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDirectMessageEvent'");
    }

    @Override
    public void handleGroupMessageEvent(PlayerToken senderToken, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGroupMessageEvent'");
    }

    @Override
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleTokenAssignmentEvent'");
    }

    @Override
    public void handleEndedTokenPhaseEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleEndedTokenPhaseEvent'");
    }

    @Override
    public void handlePlayedCardEvent(PlayerToken playerToken, int secretObjectiveCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayedCardEvent'");
    }

    @Override
    public void handlePlayerElementsEvent(PlayerToken playerToken, Map<Elements, Integer> resources) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayerElementsEvent'");
    }

    @Override
    public void handleGameError(String error) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGameError'");
    }

    @Override
    public void handleGameResultsEvent(List<Pair<PlayerToken, Integer>> gameResults) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleGameResultsEvent'");
    }

    @Override
    public void handleCardsPlayabilityEvent(PlayerToken playerToken, List<Coords> availableSlots,
            Map<Integer, List<Pair<CardSide, Boolean>>> cardsPlayability) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleCardsPlayabilityEvent'");
    }

    @Override
    public void handleLimitPointsReachedEvent(PlayerToken playerToken, int score, int limitPoints) {

    }

    @Override
    public UserInfo getUserInfo() {
        synchronized (userInfoLock) {
            return userInfo;
        }
    }

    @Override
    public void connectionToGameResult(boolean connectedToGame) {
        waitingStartGame.set(false);
    }

    @Override
    public void handleRefusedReconnection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleRefusedReconnection'");
    }

    @Override
    public void handleReconnetionToGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleReconnetionToGame'");
    }

    @Override
    public void handleAlreadyInLobbyErrorEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleAlreadyInLobbyErrorEvent'");
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Optional<Resources> nextCardSeed, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }
}

enum State {
    HOME,
    LOBBY,
    RECONNECTION,
    END,
}