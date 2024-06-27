package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.controller.usermanagement.LobbyInfo;
import it.polimi.ingsw.controller.usermanagement.User;
import it.polimi.ingsw.controller.usermanagement.UserInfo;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class LobbiesListController extends Controller {
    public List<LobbyInfo> activeLobbies = null;

    @FXML private StackPane mainStackPane;
    @FXML private StackPane lobbiesStackPane;
    @FXML private StackPane headerStackPane;

    @FXML private VBox lobbiesListVBox;

    @Override
    public void initialize() {
    }

    public void initialize(GUI gui, List<LobbyInfo> activeLobbies) {
        this.gui = gui;
        this.activeLobbies = activeLobbies;

        buildList();
    }

    public void buildList() {
        activeLobbies.stream().forEach(lobbyInfo -> {
            StackPane lobbyStackPane = new StackPane();
            lobbiesListVBox.getChildren().add(lobbyStackPane);

            if (lobbyStackPane.equals(lobbiesListVBox.getChildren().get(1)))
                VBox.setMargin(lobbyStackPane, new Insets(20, 40, 0, 30));
            else VBox.setMargin(lobbyStackPane, new Insets(5, 40, 0, 30));

            HBox hbox = new HBox();

            StackPane idStackPane = new StackPane();
            idStackPane.setPrefWidth(60);
            idStackPane.setPrefHeight(50);
            idStackPane.getChildren().add(new Text(String.valueOf(lobbyInfo.id)));
            hbox.getChildren().add(idStackPane);

            StackPane managerStackPane = new StackPane();
            managerStackPane.setPrefWidth(120);
            managerStackPane.setPrefHeight(50);
            HBox.setMargin(managerStackPane, new Insets(0, 0, 0, 85));
            managerStackPane.getChildren().add(new Text(lobbyInfo.manager.name));
            hbox.getChildren().add(managerStackPane);

            List<UserInfo> users = lobbyInfo.users.stream()
                    .filter(userInfo -> !userInfo.equals(lobbyInfo.manager))
                    .toList();

            for (int i = 0; i < 3; i++) {
                StackPane playerStackPane = new StackPane();
                playerStackPane.setPrefWidth(120);
                playerStackPane.setPrefHeight(50);
                VBox.setMargin(playerStackPane, new Insets(0, 0, 0, 0));
                playerStackPane.getChildren().add(new Text(i < users.size() ? users.get(i).name : "-"));

                hbox.getChildren().add(playerStackPane);
            }

            StackPane joinableStackPane = new StackPane();
            HBox.setMargin(joinableStackPane, new Insets(0, 0, 0, 85));
            joinableStackPane.setPrefWidth(80);
            joinableStackPane.setPrefHeight(50);
            ImageView joinableImageView = new ImageView(new Image(lobbyInfo.gameStarted ? "/images/icons/lobbyNonJoinable.png" : "/images/icons/lobbyJoinable.png"));
            joinableImageView.setFitWidth(20);
            joinableImageView.setFitHeight(20);
            joinableStackPane.getChildren().add(joinableImageView);
            hbox.getChildren().add(joinableStackPane);

            StackPane joinStackPane = new StackPane();
            HBox.setMargin(joinStackPane, new Insets(0, 0, 0, 0));
            joinStackPane.setPrefWidth(80);
            joinStackPane.setPrefHeight(50);
            joinStackPane.getChildren().add(new Button("Join"));
            hbox.getChildren().add(joinStackPane);

            lobbyStackPane.getChildren().add(hbox);
        });
    }

//    /**
//     * This method is called when the user tries to join a lobby or create one while
//     * being in another.
//     *
//     * @param message the error message
//     */
//    @Override
//    public void handleJoinLobbyError(String message) {
//        joiningLobby.set(false);
//        showPopup(message);
//    }
}
