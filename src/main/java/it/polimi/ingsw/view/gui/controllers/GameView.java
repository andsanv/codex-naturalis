package it.polimi.ingsw.view.gui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

import java.io.IOException;

public class GameView {

    public String cardSelected;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox playerHand;

    @FXML
    private GridPane gridPane;



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
        for (int i = 0; i <=41; i++) {
            for (int j = 0; j <= 41; j++) {
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
        //todo: add red layout for inaccessible cells
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
        System.out.println(idToString(stackPane.getId()));
        cardSelected = idToString(stackPane.getId());
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

        System.out.println(row + " -- " + col);
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
        Image image = new Image("/images/cards/fronts/" + cardSelected + ".png");
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
