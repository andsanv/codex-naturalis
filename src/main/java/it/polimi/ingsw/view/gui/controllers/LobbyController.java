package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.Lobby;
import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.distributed.commands.main.LeaveLobbyCommand;
import it.polimi.ingsw.distributed.commands.main.StartGameCommand;
import it.polimi.ingsw.view.gui.GUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Allows to show the single lobby, after joining.
 */
public class LobbyController extends Controller {
    /* GUI STRUCTURE */
    @FXML private StackPane mainStackPane;
    @FXML private StackPane popupStackPane;
    @FXML private StackPane listStackPane;
    @FXML private Rectangle darkOverlayRectangle;
    @FXML private Text lobbyText;
    @FXML private StackPane textPane;
    @FXML private Button startButton;
    @FXML private Button backButton;

    @FXML private StackPane firstPlayerPane;
    @FXML private Text firstPlayerText;
    @FXML private ImageView firstPlayerImageView;

    @FXML private StackPane secondPlayerPane;
    @FXML private Text secondPlayerText;
    @FXML private ImageView secondPlayerImageView;

    @FXML private StackPane thirdPlayerPane;
    @FXML private Text thirdPlayerText;
    @FXML private ImageView thirdPlayerImageView;

    @FXML private StackPane fourthPlayerPane;
    @FXML private Text fourthPlayerText;
    @FXML private ImageView fourthPlayerImageView;

    private List<StackPane> playerEntries;

    private AtomicReference<List<LobbyInfo>> activeLobbies;

    private LobbyInfo lobby;

    /**
     * Initializer for testing.
     */
    @Override
    public void initialize() {
        this.gui = new GUI();
        User self = new User("Andrea");
        gui.selfUserInfo.set(new UserInfo(self));

        Lobby tempLobby = new Lobby(self);
        lobby = new LobbyInfo(tempLobby);

        this.lobbyText.setText("Lobby #" + 5);

        firstPlayerText.setText("Andrea");
        secondPlayerText.setText("Samuele");
        thirdPlayerText.setText("Gabriele");

        firstPlayerImageView.setImage(new Image("/images/icons/crown.png"));
        secondPlayerImageView.setImage(new Image("/images/icons/user.png"));
        thirdPlayerImageView.setImage(new Image("/images/icons/user.png"));
        applyCss();
    }

    /**
     * Actual initializer for the scene.
     *
     * @param gui the gui application
     * @param lobby the lobby the user joined
     */
    public void initialize(GUI gui, LobbyInfo lobby) {
        this.gui = gui;
        this.activeLobbies = gui.availableLobbies;
        this.connectionHandler = gui.connectionHandler;
        this.selfUserInfo = gui.selfUserInfo;
        this.lobby = lobby;

        setupGui();
        applyCss();
    }

    /**
     * This method handles the reception of the active lobbies.
     * Updates the player list if it changed, quits the lobby if the user quit or changes to game scene if game started.
     *
     * @param lobbies list of lobbies
     */
    @Override
    public void handleLobbiesEvent(List<LobbyInfo> lobbies) {
        this.activeLobbies.set(lobbies);

        Optional<LobbyInfo> lobby = lobbies.stream()
                .filter(l -> l.contains(selfUserInfo.get()))
                .findFirst();

        if (lobby.isPresent()) {
            System.out.println("[DEBUG] Lobby with self user info found");
            updatePlayers(lobby.get());
            return;
        }

        lobby = lobbies.stream()
                .filter(l -> l.users.contains(selfUserInfo.get()))
                .findFirst();

        if (lobby.isEmpty()) {
            System.out.println("[DEBUG] Correctly exited from previous lobby");
             gui.changeToMenuScene();
             return;
        }

        lobbies.stream()
                .filter(l -> l.id == this.lobby.id)
                .filter(l -> l.gameStarted)
                .findFirst()
                .ifPresent(l -> {
                    // gui.changeToGameScene(lobby.users);
                });
    }

    @Override
    public void handleStartGameError(String error) {
        System.out.println("[ERROR] " + error);
    }

    /**
     * Handles the action of pressing the start button.
     *
     * @param actionEvent the ActionEvent event
     */
    public void startGame(ActionEvent actionEvent) {
        gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToMainServer(new StartGameCommand(selfUserInfo.get(), lobby.id));
            System.out.println("[INFO] Submitted StartGameCommand");
        });
    }

    /**
     * Handles the action of pressing the back button.
     *
     * @param event the ActionEvent event
     */
    public void handleBackButton(ActionEvent event) {
        gui.submitToExecutorService(() -> {
            connectionHandler.get().sendToMainServer(new LeaveLobbyCommand(gui.selfUserInfo.get(), lobby.id));
            System.out.println("[INFO] Submitted LeaveLobbyCommand");
        });
    }

    /**
     * Allows to update the players list, GUI side.
     *
     * @param lobby the lobby the user is in
     */
    public void updatePlayers(LobbyInfo lobby) {
        lobby.users.forEach(user -> {
            HBox playerHBox = (HBox) playerEntries.get(lobby.users.indexOf(user)).getChildren().getFirst();

            ((Text) ((StackPane) playerHBox.getChildren().get(0)).getChildren().getFirst()).setText(user.name);
            if (lobby.manager.equals(user)) {
                ((ImageView) (((StackPane) playerHBox.getChildren().get(1)).getChildren().getFirst())).setImage(new Image("/images/icons/crown.png"));
                startButton.setDisable(false);
            }
            else ((ImageView) ((StackPane) playerHBox.getChildren().get(1)).getChildren().getFirst()).setImage(new Image("/images/icons/user.png"));
        });

        for (int i = lobby.users.size(); i < playerEntries.size(); i++) {
            HBox playerHBox = (HBox) playerEntries.get(i).getChildren().getFirst();

            ((Text) ((StackPane) playerHBox.getChildren().get(0)).getChildren().getFirst()).setText("-");
            ((ImageView) (((StackPane) playerHBox.getChildren().get(1)).getChildren().getFirst())).setImage(null);
        }
    }


    public void setupGui() {
        playerEntries = new ArrayList<>(Arrays.asList(firstPlayerPane, secondPlayerPane, thirdPlayerPane, fourthPlayerPane));

        backButton.setOnAction(this::handleBackButton);
        startButton.setOnAction(this::startGame);
    }

    /**
     * Applies CSS to the view.
     */
    public void applyCss() {
        mainStackPane.getStyleClass().add("main-pane");

        lobbyText.getStyleClass().add("lobby-text");

        darkOverlayRectangle.getStyleClass().add("dark-overlay-rectangle");
        popupStackPane.getStyleClass().add("popup-pane");

        firstPlayerPane.getStyleClass().add("player-pane");
        secondPlayerPane.getStyleClass().add("player-pane");
        thirdPlayerPane.getStyleClass().add("player-pane");
        fourthPlayerPane.getStyleClass().add("player-pane");

        backButton.getStyleClass().add("button-back");
        startButton.getStyleClass().add("button-start");

        textPane.getStyleClass().add("text-pane");
    }
}
