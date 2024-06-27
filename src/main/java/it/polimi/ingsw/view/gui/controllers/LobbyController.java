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


public class LobbyController extends Controller {
    /* STRUCTURE */
    @FXML private StackPane mainStackPane;
    @FXML private StackPane popupStackPane;
    @FXML private StackPane listStackPane;
    @FXML private Rectangle darkOverlayRectangle;
    @FXML private Text lobbyText;
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

    private LobbyInfo lobby;

    /**
     * Initializer for testing.
     */
    public void initialize() {
        this.gui = new GUI();
        User self = new User("Andrea");
        gui.selfUserInfo = new UserInfo(self);

        Lobby tempLobby = new Lobby(self);
        lobby = new LobbyInfo(tempLobby);

        this.lobbyText.setText("Lobby #" + 5);

        firstPlayerText.setText("Andrea");
        secondPlayerText.setText("Samuele");
        thirdPlayerText.setText("Gabriele");

        firstPlayerImageView.setImage(new Image("/images/icons/crown.png"));
        secondPlayerImageView.setImage(new Image("/images/icons/user.png"));
        thirdPlayerImageView.setImage(new Image("/images/icons/user.png"));
    }

    /**
     * Actual initializer for the scene.
     *
     * @param gui the gui application
     * @param lobby the lobby the user joined
     */
    public void initialize(GUI gui, LobbyInfo lobby) {
        this.gui = gui;

        this.lobby = lobby;
        playerEntries = new ArrayList<>(Arrays.asList(firstPlayerPane, secondPlayerPane, thirdPlayerPane, fourthPlayerPane));

        // updatePlayers(lobby);
        // lobbyText.setText("Lobby #" + lobby.id);

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
        Optional<LobbyInfo> lobby = lobbies.stream()
                .filter(l -> l.contains(gui.selfUserInfo))
                .findFirst();

        if (lobby.isPresent()) {
            updatePlayers(lobby.get());
            return;
        }

         lobby = lobbies.stream()
                .filter(l -> l.id == this.lobby.id)
                .filter(l -> !l.users.contains(gui.selfUserInfo))
                .findFirst();

         if (lobby.isPresent()) {
             gui.changeToMenuScene();
             return;
         }

        lobbies.stream()
                .filter(l -> l.id == this.lobby.id)
                .filter(l -> l.gameStarted)
                .findFirst()
                .ifPresent(l -> {
                    //gui.changeToGameScene(lobby.users);
                });
    }

    /**
     * Handles the action of pressing the start button.
     *
     * @param actionEvent the ActionEvent event
     */
    public void startGame(ActionEvent actionEvent) {
        gui.connectionHandler.sendToMainServer(new StartGameCommand(gui.selfUserInfo, lobby.id));
    }

    /**
     * Handles the action of pressing the back button.
     *
     * @param event the ActionEvent event
     */
    public void handleBackButton(ActionEvent event) {
        gui.connectionHandler.sendToMainServer(new LeaveLobbyCommand(gui.selfUserInfo, lobby.id));
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
    }

    public void setupGui() {
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
    }
}
