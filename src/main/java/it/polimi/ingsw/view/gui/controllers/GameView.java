package it.polimi.ingsw.view.gui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.util.Stack;

public class GameView {

    public String sideSelected = "fronts";
    public String cardSelectedPath;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox playerHand;

    @FXML
    private GridPane gridPane;

    @FXML
    private VBox playerListPane;




    private Scale scaleTransform;
    private double scaleValue = 1;
    private final double scaleIncrement = 0.1;

    @FXML
    public void initialize() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gui/gameView2.fxml"));

        scrollPane.prefViewportWidthProperty().bind(gridPane.widthProperty());
        scrollPane.prefViewportHeightProperty().bind(gridPane.heightProperty());
        StackPane.setAlignment(scrollPane, Pos.CENTER);
        // Initialize scale transform
        scaleTransform = new Scale(scaleValue, scaleValue);
        stackPane.getTransforms().add(scaleTransform);

        //TODO: may be to remove
        for (Node node : playerHand.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackCard = (StackPane) node;
                stackCard.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }

        //Create grid images containers
        for (int i = 0; i <=40; i++) {
            for (int j = 0; j <= 40; j++) {
                StackPane cell = new StackPane();
                if ((i+j) % 2 == 0) {
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(331);
                    imageView.setFitWidth(496.5);
                    imageView.setPreserveRatio(true);
                    cell.getChildren().add(imageView);
                }
                gridPane.add(cell, i, j);
                GridPane.setHalignment(cell, HPos.CENTER); // Horizontal alignment
                GridPane.setValignment(cell, VPos.CENTER); // Vertical alignment
                GridPane.setHgrow(cell, Priority.NEVER);  // Horizontal grow
                GridPane.setVgrow(cell, Priority.NEVER); // Vertical grow
            }
        }


        //set events handling methods for stack panes in player hand
        //todo: insert red layout for inaccessible cells
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {

                node.setOnMouseEntered(mouseEvent -> {
                    try {
                        boardCellHoverEnter(mouseEvent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                node.setOnMouseExited(mouseEvent -> {
                    try {
                        boardCellHoverExit(mouseEvent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        }

        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;

                // Check if the stackPane has any children
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().get(0) instanceof ImageView) {
                    stackPane.setOnMouseClicked(mouseEvent -> {
                        try {
                            setCardOnClick(mouseEvent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

        //side pane for player list
        for (int i = 0; i < 2; i++) {
            HBox playerPane = new HBox();
            ImageView token = new ImageView();
            token.setFitHeight(40);
            token.setFitWidth(40);
            token.setPreserveRatio(true);
            ImageView view = new ImageView();
            view.setFitHeight(40);
            view.setFitWidth(40);
            view.setPreserveRatio(true);
            Image tokenImg = new Image("/images/tokens/token_blue.png");
            Image viewImg = new Image("/images/icons/view_icon.png");
            token.setImage(tokenImg);
            view.setImage(viewImg);
            int points = 0;
            Text nickname = new Text("Player " + i);
            Text score = new Text( (String) (points + " pts") );
            nickname.setFont(javafx.scene.text.Font.font("System", 28));
            score.setFont(javafx.scene.text.Font.font("System", 28));
            playerPane.setSpacing(20);
            playerPane.setAlignment(Pos.CENTER);
            playerPane.setPadding(new Insets(10));
            playerPane.getChildren().addAll(token, nickname, score, view);
            playerListPane.getChildren().add(playerPane);
            playerListPane.setSpacing(20);
        }

        // Add zoom in/out event handling
        Scene scene = scrollPane.getScene();
        if (scene != null) {
            setupKeyEvents(scene);
        } else {
            scrollPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    setupKeyEvents(newScene);
                }
            });
        }

    }

    @FXML
    public void selectCardOnClick(javafx.scene.input.MouseEvent mouseEvent) throws IOException {

        StackPane stackPane = (StackPane) mouseEvent.getSource();
        ImageView imageView = (ImageView) stackPane.getChildren().get(0);
        String imagePath = imageView.getImage().getUrl();
        int startIndex = imagePath.indexOf("/images");
        imagePath = imagePath.substring(startIndex);
        cardSelectedPath = imagePath;
    }

    @FXML
    public void flipHandCard(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        Button button = (Button) mouseEvent.getSource();
        StackPane stackPane = (StackPane) ((VBox) button.getParent()).getChildren().get(1);
        ImageView imageView = (ImageView) stackPane.getChildren().get(0);

        String filepath = imageView.getImage().getUrl();
        int startIndex = filepath.indexOf("/images");
         filepath = filepath.substring(startIndex);

        // Change "fronts" to "backs" and vice versa
        if (filepath.contains("fronts")) {
            filepath = filepath.replace("fronts", "backs");
        } else if (filepath.contains("backs")) {
            filepath = filepath.replace("backs", "fronts");
        }

        cardSelectedPath = filepath;
        Image image = new Image(cardSelectedPath);
        imageView.setImage(image);
    }

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case PLUS:
                case EQUALS: // Handle both + and = (shift + = is + on some keyboards)
                    //zoomIn();
                    break;
                case MINUS:
                    //zoomOut();
                    break;
                default:
                    break;
            }
        });
    }

    private void zoomIn() {
        scaleValue += scaleIncrement;
        applyScale();
    }

    private void zoomOut() {
        scaleValue -= scaleIncrement;
        applyScale();
    }

    private void applyScale() {
        double oldHValue = scrollPane.getHvalue();
        double oldVValue = scrollPane.getVvalue();

        scaleTransform.setX(scaleValue);
        scaleTransform.setY(scaleValue);

        double contentWidth = gridPane.getBoundsInLocal().getWidth();
        double contentHeight = gridPane.getBoundsInLocal().getHeight();

        // Calculate new scroll positions to keep the viewport centered
        double newHValue = (oldHValue + 0.5 * scrollPane.getViewportBounds().getWidth() / contentWidth) * (contentWidth - scrollPane.getViewportBounds().getWidth()) / (contentWidth - scaleValue * scrollPane.getViewportBounds().getWidth());
        double newVValue = (oldVValue + 0.5 * scrollPane.getViewportBounds().getHeight() / contentHeight) * (contentHeight - scrollPane.getViewportBounds().getHeight()) / (contentHeight - scaleValue * scrollPane.getViewportBounds().getHeight());

        scrollPane.setHvalue(newHValue);
        scrollPane.setVvalue(newVValue);
    }

    @FXML
    public void handCardHoverEnter(javafx.scene.input.MouseEvent mouseEvent) throws IOException {

        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: yellow; -fx-border-width: 10;");
        sourceImageView.applyCss();
    }

    @FXML
    public void handCardHoverExit(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        StackPane sourceImageView = (StackPane) mouseEvent.getSource();
        sourceImageView.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        sourceImageView.applyCss();
    }

    @FXML
    public void boardCellHoverEnter(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        StackPane stackPane = (StackPane) mouseEvent.getSource();
        int row =  GridPane.getRowIndex(stackPane);
        int col =  GridPane.getColumnIndex(stackPane);

        if ((row + col) %2 == 0) {
            stackPane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
        }
        else {
            stackPane.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        }
        stackPane.applyCss();
    }

    @FXML
    public void boardCellHoverExit(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        StackPane stackPane = (StackPane) mouseEvent.getSource();
        stackPane.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        stackPane.applyCss();
    }

    @FXML
    public void setCardOnClick(javafx.scene.input.MouseEvent mouseEvent) throws IOException {

        bringStackPaneToFront(gridPane, (StackPane) mouseEvent.getSource());
        ImageView imageView = (ImageView) (((StackPane) mouseEvent.getSource()).getChildren().get(0));
        System.out.println(cardSelectedPath);
        Image image = new Image(cardSelectedPath);
        imageView.setImage(image);
    }

    public void bringStackPaneToFront(GridPane gridPane, StackPane stackPaneToFront) {
        // Get the children list of the GridPane
        ObservableList<Node> children = gridPane.getChildren();

        // Check if the StackPane is in the GridPane
        if (children.contains(stackPaneToFront)) {
            // Remove the StackPane from its current position
            children.remove(stackPaneToFront);

            // Add the StackPane back to the end of the list
            children.add(stackPaneToFront);
        } else {
            System.out.println("The specified StackPane is not a child of the GridPane.");
        }
    }

    public String idToString (String id) {
        //TODO change it for general cards
        String nameFile = "";
        System.out.println(id);
        switch (id) {
            case "firstHandCard":
                nameFile = "000";
                break;
            case "secondHandCard":
                nameFile = "010";
                break;
            case "thirdHandCard":
                nameFile = "020";
                break;
            default:
                nameFile = "000";

        }
        return nameFile;
    }


}
