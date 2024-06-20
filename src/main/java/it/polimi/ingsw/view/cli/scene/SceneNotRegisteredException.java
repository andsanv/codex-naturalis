package it.polimi.ingsw.view.cli.scene;

/**
 * Runtime exception thrown when the requested scene hasn't been registered.
 */
public class SceneNotRegisteredException extends RuntimeException {
    public SceneNotRegisteredException(String message) {
        super(message);
    }
}