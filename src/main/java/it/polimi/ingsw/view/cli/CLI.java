package it.polimi.ingsw.view.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.client.ConnectionHandler;
import it.polimi.ingsw.model.SlimGameModel;
import it.polimi.ingsw.model.card.CardSide;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.player.Coords;
import it.polimi.ingsw.model.player.PlayerToken;
import it.polimi.ingsw.util.Pair;
import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.UserInfoManager;
import it.polimi.ingsw.view.cli.scene.SceneManager;
import it.polimi.ingsw.view.cli.scene.scenes.AccountScene;
import it.polimi.ingsw.view.cli.scene.scenes.ConnectionLostScene;
import it.polimi.ingsw.view.cli.scene.scenes.ConnectionScene;
import it.polimi.ingsw.view.cli.scene.scenes.GameScene;
import it.polimi.ingsw.view.cli.scene.scenes.HomeScene;
import it.polimi.ingsw.view.cli.scene.scenes.LobbiesScene;
import it.polimi.ingsw.view.cli.scene.scenes.ObjectiveCardScene;
import it.polimi.ingsw.view.cli.scene.scenes.StarterCardScene;
import it.polimi.ingsw.view.cli.scene.scenes.TokenSelectionScene;
import it.polimi.ingsw.view.cli.scene.scenes.UserInfoLoginScene;

public class CLI implements UI {
    public static void main(String[] args) {
        new CLI();
    }

    /**
     * Thread for handling user input
     */
    private Thread userInputThread;

    /**
     * The connection handler used to communicate with the server
     */
    private ConnectionHandler connectionHandler = null;

    /**
     * The manager of the CLI scenes
     */
    private final SceneManager sceneManager = new SceneManager(this);

    /**
     * The only input scanner used by the CLI
     */
    private Scanner scanner;

    /**
     * The UserInfo of the user that is logged-in.
     */
    private UserInfo userInfo;

    /**
     * An Object for synchronizing userInfo
     */
    private final Object userInfoLock = new Object();

    /**
     * A list of all active lobbies.
     */
    private List<LobbyInfo> lobbies;

    /**
     * An Object for synchronizing lobbies
     */
    private final Object lobbiesLock = new Object();

    /**
     * The id of the current lobby
     */
    public final AtomicInteger lobbyId = new AtomicInteger(-1);

    /**
     * Thread-safe boolean that is true if the CLI is waiting for the server to send
     * the UserInfo used to make the following requests.
     */
    public final AtomicBoolean waitinLogin = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message that the user can receive
     * while logging-in.
     */
    public final AtomicReference<String> waitingLoginError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true if the CLI is waiting for the server to send
     * the list of available lobbies.
     */
    public final AtomicBoolean waitingLobbies = new AtomicBoolean(false);

    /**
     * Thread-safe boolean that is true while the user is joining a lobby.
     */
    public final AtomicBoolean joiningLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if the user couldn't join
     * the lobby.
     */
    public final AtomicReference<String> joiningLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is creating a lobby.
     */
    public final AtomicBoolean creatingLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if creating the lobby went
     * wrong.
     */
    public final AtomicReference<String> creatingLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is leaving a lobby.
     */
    public final AtomicBoolean leavingLobby = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if the user couldn't leave
     * the lobby.
     */
    public final AtomicReference<String> leavingLobbyError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true while the user is starting a game.
     */
    public final AtomicBoolean startingGame = new AtomicBoolean(false);

    /**
     * Thread-safe string that contains an error message if start game failed.
     */
    public final AtomicReference<String> startingGameError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true if the user is in a match.
     */
    public final AtomicBoolean inGame = new AtomicBoolean(false);

    /**
     * List of players in the match.
     */
    public final AtomicReference<List<UserInfo>> usersInGame = new AtomicReference<>(null);

    /**
     * Thread-safe string that contains an error message if the user couldn't do a
     * command successfully.
     */
    public final AtomicReference<String> lastGameError = new AtomicReference<>(null);

    /**
     * Thread-safe boolean that is true if the user is waiting for a sent command to
     * be executed by the server.
     */
    public final AtomicBoolean waitingGameEvent = new AtomicBoolean(false);

    /**
     * CountDownLatch for waiting for the token phase to end.
     */
    public CountDownLatch tokenPhaseLatch = new CountDownLatch(1);

    /**
     * CountDownLatch for waiting for the token phase to end.
     */
    public CountDownLatch objectivePhaseLatch = new CountDownLatch(1);

    /**
     * CountDownLatch for waiting reconnection.
     */
    public CountDownLatch gameReconnectionLatch = new CountDownLatch(1);

    /**
     * Mapping between players and tokens
     */
    private Map<PlayerToken, UserInfo> tokenToUser = new HashMap<>();

    /**
     * Object for synchronizing access to the map tokenToUser
     */
    private final Object tokenToUserLock = new Object();

    /**
     * Token that the player has selected.
     */
    public final AtomicReference<PlayerToken> token = new AtomicReference<>(null);

    /**
     * Atomic refererce for the starter card.
     */
    public final AtomicReference<Pair<Integer, CardSide>> starterCard = new AtomicReference<>(null);

    /**
     * Atomic refererce for the drawn secret objectives.
     */
    public final AtomicReference<Pair<Integer, Integer>> secretObjectives = new AtomicReference<>(null);

    /**
     * List with direct messages saved as a pair with the sender of the direct
     * message and the message itself
     */
    private List<Pair<UserInfo, String>> directMessages = new ArrayList<>();

    /**
     * Direct messages object for synchronization
     */
    public final Object directMessagesLock = new Object();

    /**
     * List with group messages saved as a pair with the sender of the group message
     * and the message itself
     */
    private List<Pair<UserInfo, String>> groupMessages = new ArrayList<>();

    /**
     * Group messages object for synchronization
     */
    public final Object groupMessagesLock = new Object();

    /**
     * SlimGameModel of the current match
     */
    private SlimGameModel slimGameModel;

    /**
     * Object to correctly synchronize when accessing the SlimGameModel
     */
    public final Object slimGameModelLock = new Object();

    /**
     * Cli private constructor, can only be called by the main static method in this
     * class.
     * Inits helper threads and creates the needed scenes.
     */
    private CLI() {
        /*
         * Register scenes in the SceneManager
         */
        sceneManager.registerScene(new HomeScene(sceneManager));
        sceneManager.registerScene(new ConnectionScene(sceneManager));
        sceneManager.registerScene(new ConnectionLostScene(sceneManager));
        sceneManager.registerScene(new UserInfoLoginScene(sceneManager));
        sceneManager.registerScene(new AccountScene(sceneManager));
        sceneManager.registerScene(new LobbiesScene(sceneManager));
        sceneManager.registerScene(new TokenSelectionScene(sceneManager));
        sceneManager.registerScene(new StarterCardScene(sceneManager));
        sceneManager.registerScene(new ObjectiveCardScene(sceneManager));
        sceneManager.registerScene(new GameScene(sceneManager));

        /*
         * Init and start the SceneManager
         */
        sceneManager.setInitialScene(HomeScene.class);
        sceneManager.start();

        /*
         * Start the user input thread
         */
        startUserInputHandler();
    }

    /**
     * Handles user input and can be safely interrupted
     */
    private void startUserInputHandler() {
        scanner = new Scanner(System.in);

        userInputThread = new Thread(() -> {
            try {
                while (sceneManager.isRunning.get()) {
                    System.out.print("> ");
                    System.out.flush();
                    String input = scanner.nextLine();
                    sceneManager.handleInput(input);
                }
            } catch (Exception e) {
                // Thread is volountarily stopped so we don't have to do anything
            }
            scanner.close();
        });

        userInputThread.start();
    }

    /**
     * Resets the prompt on the current line.
     */
    private void resetPrompt() {
        System.out.print(ansi().reset().eraseLine().cursorToColumn(0).a("> "));
        System.out.flush();
    }

    /**
     * Method called when the game ends.
     */
    public void resetAttributesAfterMatch() {
        synchronized (tokenToUserLock) {
            tokenToUser.clear();
        }

        starterCard.set(null);

        directMessages.clear();
        groupMessages.clear();

        lastGameError.set(null);

        tokenPhaseLatch = new CountDownLatch(1);
        objectivePhaseLatch = new CountDownLatch(1);
    }

    /**
     * Direct messages getter
     * 
     * @return a copy of the list of direct messages
     */
    public List<Pair<UserInfo, String>> getDirectMessages() {
        synchronized (directMessagesLock) {
            return new ArrayList<>(directMessages);
        }
    }

    /**
     * Group messages getter
     * 
     * @return a copy of the list of group messages
     */
    public List<Pair<UserInfo, String>> getGroupMessages() {
        synchronized (groupMessagesLock) {
            return new ArrayList<>(groupMessages);
        }
    }

    /**
     * Checks if the given token is available
     * 
     * @return true if the token is available for selection
     */
    public boolean isTokenAvailable(PlayerToken token) {
        synchronized (tokenToUserLock) {
            return !tokenToUser.containsKey(token);
        }
    }

    /**
     * Token to user map getter.
     * 
     * @return a copy of the token to user map
     */
    public Map<PlayerToken, UserInfo> getTokenToPlayerMap() {
        synchronized (tokenToUserLock) {
            return new HashMap<>(tokenToUser);
        }
    }

    /**
     * Returns the list of available tokens.
     * 
     * @return a List of PlayerTokens that are available for selection.
     */
    public List<PlayerToken> getAvailableTokens() {
        List<PlayerToken> allTokens = Arrays.asList(PlayerToken.values());

        synchronized (tokenToUserLock) {
            return allTokens.stream().filter(t -> !tokenToUser.containsKey(t)).collect(Collectors.toList());
        }
    }

    /**
     * ConnectionHandler setter.
     * 
     * @param connectionHandler the ConnectionHandler that the CLI will use
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * ConnectionHandler getter.
     * 
     * @return the ConnectionHandler used by the CLI.
     */
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    /**
     * UserInfo setter.
     * 
     * @param userInfo the new UserInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
        }
    }

    /**
     * Lobbies getter as copy.
     */
    public List<LobbyInfo> getLobbies() {
        synchronized (lobbiesLock) {
            return lobbies != null ? new ArrayList<>(lobbies) : null;
        }
    }

    @Override
    public void handleLoginEvent(UserInfo userInfo, String error) {
        synchronized (userInfoLock) {
            this.userInfo = userInfo;
            UserInfoManager.saveUserInfo(userInfo);
        }

        waitingLoginError.set(error);
        waitinLogin.set(false);
    }

    @Override
    public void handleServerError(String error) {
        CLIPrinter.displayError(error);
    }

    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        synchronized (lobbiesLock) {
            this.lobbies = lobbies;

            UserInfo userInfo = this.getUserInfo();

            lobbies.stream()
                    .filter(lobby -> lobby.contains(userInfo))
                    .findAny()
                    .ifPresentOrElse(
                            lobby -> {
                                lobbyId.set(lobby.id);

                                joiningLobby.set(false);

                                if (lobby.manager.equals(userInfo)) {
                                    creatingLobby.set(false);
                                }
                            },
                            () -> {
                                leavingLobby.set(false);
                                lobbyId.set(-1);
                            });
        }
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
    public void handleDrawnStarterCardEvent(PlayerToken playerToken, int drawnCardId) {
        if (playerToken != this.token.get())
            return;

        starterCard.set(new Pair<Integer, CardSide>(drawnCardId, null));
        waitingGameEvent.set(false);
    }

    @Override
    public void handleChosenStarterCardSideEvent(PlayerToken playerToken, CardSide cardSide) {
        if (playerToken != this.token.get())
            return;

        starterCard.set(new Pair<Integer, CardSide>(starterCard.get().first, cardSide));
        waitingGameEvent.set(false);
    }

    @Override
    public void handleEndedStarterCardPhaseEvent() {
        waitingGameEvent.set(false);

        if (sceneManager.getCurrentScene() != StarterCardScene.class) {
            sceneManager.transition(StarterCardScene.class);
            resetPrompt();
        }
    }

    @Override
    public void handleDrawnObjectiveCardsEvent(PlayerToken playerToken, int drawnCardId, int secondDrawnCardId) {
        secretObjectives.set(new Pair<Integer, Integer>(drawnCardId, secondDrawnCardId));

        waitingGameEvent.set(false);
        tokenPhaseLatch.countDown();

        if (sceneManager.getCurrentScene() != StarterCardScene.class) {
            sceneManager.transition(StarterCardScene.class);
            resetPrompt();
        }
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
    public void handleTokenAssignmentEvent(UserInfo player, PlayerToken assignedToken) {
        synchronized (tokenToUserLock) {
            tokenToUser.put(assignedToken, player);
        }

        if (getUserInfo().equals(player)) {
            waitingGameEvent.set(false);
        }
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
        lastGameError.set(error);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLimitPointsReachedEvent'");
    }

    @Override
    public UserInfo getUserInfo() {
        synchronized (userInfoLock) {
            return userInfo != null ? new UserInfo(userInfo) : null;
        }
    }

    @Override
    public void handleEndedInitializationPhaseEvent(SlimGameModel slimGameModel) {
        synchronized (slimGameModelLock) {
            this.slimGameModel = slimGameModel;
        }

        

        if (sceneManager.getCurrentScene() != GameScene.class) {
            sceneManager.transition(GameScene.class);
        }
    }

    @Override
    public void handleJoinLobbyError(String message) {
        joiningLobbyError.set(message);
        joiningLobby.set(false);
    }

    @Override
    public void handleStartGameError(String message) {
        startingGameError.set(message);
        startingGame.set(false);
    }

    @Override
    public void handleCreateLobbyError(String message) {
        creatingLobbyError.set(message);
        creatingLobby.set(false);
    }

    @Override
    public void handleLeaveLobbyError(String message) {
        leavingLobbyError.set(message);
        leavingLobby.set(false);
    }

    @Override
    public void handleDrawnGoldDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnGoldDeckCardEvent'");
    }

    @Override
    public void handleDrawnResourceDeckCardEvent(PlayerToken playerToken, int drawnCardId, boolean deckEmptied,
            Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnResourceDeckCardEvent'");
    }

    @Override
    public void handleDrawnVisibleResourceCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleResourceCardEvent'");
    }

    @Override
    public void handleDrawnVisibleGoldCardEvent(PlayerToken playerToken, int drawnCardPosition, int drawnCardId,
            Integer replacementCardId, boolean deckEmptied, Integer nextCardId, int handIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDrawnVisibleGoldCardEvent'");
    }

    @Override
    public void handleReconnetionToGame(SlimGameModel slimModel, Map<UserInfo, PlayerToken> userToToken) {
        synchronized (slimGameModelLock) {
            this.slimGameModel = slimModel;
        }

        synchronized (tokenToUserLock) {
            tokenToUser = userToToken.entrySet().stream()
                    .peek(e -> {
                        // Update player token with the selected one
                        if (e.getKey().equals(getUserInfo())) {
                            token.set(e.getValue());
                        }
                    })
                    .collect(Collectors.toMap(Entry::getValue, Entry::getKey));
        }
    }

    @Override
    public void handlePlayerTurnEvent(PlayerToken currentPlayer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePlayerTurnEvent'");
    }

    @Override
    public void handleLastRoundEvent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLastRoundEvent'");
    }

    @Override
    public void handleDirectMessageEvent(UserInfo sender, UserInfo receiver, String message) {
        if (!sender.equals(getUserInfo()))
            return;

        synchronized (directMessagesLock) {
            directMessages.add(new Pair<UserInfo, String>(sender, message));
        }
    }

    @Override
    public void handleGroupMessageEvent(UserInfo sender, String message) {
        synchronized (groupMessagesLock) {
            groupMessages.add(new Pair<UserInfo, String>(sender, message));
        }
    }

    @Override
    public void handleGameStartedEvent(List<UserInfo> users) {
        this.startingGame.set(false);
        this.usersInGame.set(users);

        sceneManager.transition(TokenSelectionScene.class);
        resetPrompt();
    }

    @Override
    public void handleEndedTokenPhaseEvent(Map<UserInfo, PlayerToken> userInfoToToken, boolean timeLimitReached) {
        synchronized (tokenToUserLock) {
            tokenToUser = userInfoToToken.entrySet().stream()
                    .peek(e -> {
                        // Update player token with the selected one
                        if (e.getKey().equals(getUserInfo())) {
                            token.set(e.getValue());
                        }
                    })
                    .collect(Collectors.toMap(Entry::getValue, Entry::getKey));
        }

        waitingGameEvent.set(false);
        tokenPhaseLatch.countDown();

        if (sceneManager.getCurrentScene() != StarterCardScene.class) {
            sceneManager.transition(StarterCardScene.class);
            resetPrompt();
        }
    }

    @Override
    public void handleDisconnection() {
        if (sceneManager.isRunning.get() && sceneManager.getCurrentScene() != ConnectionLostScene.class) {
            sceneManager.transition(ConnectionLostScene.class);
            resetPrompt();
        }
    }
}