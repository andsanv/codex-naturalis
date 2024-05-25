package it.polimi.ingsw.view.gui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
                ImageView imageView = new ImageView();
                imageView.setFitHeight(331);
                imageView.setFitWidth(496.5);
                imageView.setPreserveRatio(true);
                if (i==1 && j==1) {
                    Image image = new Image("/images/cards/fronts/001.png");
                    imageView.setImage(image);
                    StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);
                }
                cell.getChildren().add(imageView);
                gridPane.add(cell, i, j);
                GridPane.setHalignment(cell, HPos.CENTER); // Horizontal alignment
                GridPane.setValignment(cell, VPos.CENTER); // Vertical alignment
                GridPane.setHgrow(cell, Priority.NEVER);  // Horizontal grow
                GridPane.setVgrow(cell, Priority.NEVER); // Vertical grow
            }
        }


        //make grid cells light up
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
                System.out.println("StackPane Is a children of gridPane");

                // Check if the stackPane has any children
                if (!stackPane.getChildren().isEmpty() && stackPane.getChildren().get(0) instanceof ImageView) {
                    System.out.println("ImageView Is a children of stackPane");
                    stackPane.setOnMouseClicked(mouseEvent -> {
                        try {
                            setCardOnClick(mouseEvent);
                        } catch (IOException e) {
                            // Log the error with a message
                            e.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("StackPane does not contain an ImageView as the first child.");
                }
            }
            else {
                System.out.println("Node is not an instance of StackPane or Group it is:" + node);
            }
        }




        // Add key event handling
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
        sourceImageView.setStyle("-fx-border-color: blue; -fx-border-width: 10;");
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
        stackPane.setStyle("-fx-border-color: blue; -fx-border-width: 2;");
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
        Image image = new Image("/images/cards/fronts/001.png");
        imageView.setImage(image);
        System.out.println("Image set!");
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


}
