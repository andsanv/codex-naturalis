package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateGameMenu {

    @FXML
    private Button backButton;

    @FXML
    private Text lobbyGameName;

    @FXML
    private Text player1Name;

    @FXML
    private Text player2Name;

    @FXML
    private Text player3Name;

    @FXML
    private Text player4Name;

    @FXML
    private Button startGameButton;

    @FXML
    void handleBackButtonClick(MouseEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/menuView.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Main Menu");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleStartButtonClick(MouseEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/gameView.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("");
            String url = Objects.requireNonNull(getClass().getResource("/css/bgPane.css")).toExternalForm();
            scene.getStylesheets().add(url);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setter methods for text fields
    public void setLobbyGameName(String text) {
        lobbyGameName.setText(text);
    }

    public void setPlayer1Name(String text) {
        player1Name.setText(text);
    }

    public void setPlayer2Name(String text) {
        player2Name.setText(text);
    }

    public void setPlayer3Name(String text) {
        player3Name.setText(text);
    }

    public void setPlayer4Name(String text) {
        player4Name.setText(text);
    }

    // Method to disable the start game button
    public void disableStartGameButton() {
        startGameButton.setDisable(true);
    }

    public void enableStartGameButton() {
        startGameButton.setDisable(false);
    }
}
