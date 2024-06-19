package it.polimi.ingsw.view.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class JoinGameMenu {

    @FXML
    private Button backButton;

    @FXML
    private Button joinGameButton;


    @FXML
    private TableView<LobbyGame> tableView;

    @FXML
    public void initialize()  {
        initializeTable();

    }

    public void initializeTable() {

        TableColumn<LobbyGame, String> lobbyGameNameColumn = new TableColumn<>("Lobby Game Name");
        lobbyGameNameColumn.setCellValueFactory(new PropertyValueFactory<>("lobbyGameName"));

        TableColumn<LobbyGame, Integer> numPlayersColumn = new TableColumn<>("Players");
        numPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("numPlayers"));

        TableColumn<LobbyGame, String> player1Column = new TableColumn<>("Player 1");
        player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));

        TableColumn<LobbyGame, String> player2Column = new TableColumn<>("Player 2");
        player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));

        TableColumn<LobbyGame, String> player3Column = new TableColumn<>("Player 3");
        player3Column.setCellValueFactory(new PropertyValueFactory<>("player3"));

        TableColumn<LobbyGame, String> player4Column = new TableColumn<>("Player 4");
        player4Column.setCellValueFactory(new PropertyValueFactory<>("player4"));

        tableView.getColumns().addAll(lobbyGameNameColumn, numPlayersColumn, player1Column, player2Column, player3Column, player4Column);

        //todo: add method to create LobbyGames
        ObservableList<LobbyGame> data = FXCollections.observableArrayList(
                new LobbyGame("Game 1", 2, "Angelo", "Gabriele", "", ""),
                new LobbyGame("Game 2", 4, "Charlie", "David", "Eve", "Frank")
        );
        tableView.setItems(data);

        // Handle row selection
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected lobby game: " + newSelection.getLobbyGameName());
            }
        });
    }

    public static class LobbyGame {
        private final SimpleStringProperty lobbyGameName;
        private final SimpleIntegerProperty numPlayers;
        private final SimpleStringProperty player1;
        private final SimpleStringProperty player2;
        private final SimpleStringProperty player3;
        private final SimpleStringProperty player4;

        public LobbyGame(String lobbyGameName, int numPlayers, String player1, String player2, String player3, String player4) {
            this.lobbyGameName = new SimpleStringProperty(lobbyGameName);
            this.numPlayers = new SimpleIntegerProperty(numPlayers);
            this.player1 = new SimpleStringProperty(player1);
            this.player2 = new SimpleStringProperty(player2);
            this.player3 = new SimpleStringProperty(player3);
            this.player4 = new SimpleStringProperty(player4);
        }

        public String getLobbyGameName() {
            return lobbyGameName.get();
        }

        public int getNumPlayers() {
            return numPlayers.get();
        }

        public String getPlayer1() {
            return player1.get();
        }

        public String getPlayer2() {
            return player2.get();
        }

        public String getPlayer3() {
            return player3.get();
        }

        public String getPlayer4() {
            return player4.get();
        }
    }

    @FXML
    void handleBackButtonClick(MouseEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/mainMenu.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleJoinButtonClick(MouseEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/mainMenu.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
