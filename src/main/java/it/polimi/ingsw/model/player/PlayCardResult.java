package it.polimi.ingsw.model.player;

/**
 * This enum contains the possible results of trying to place a card on the board.
 * @see PlayerBoard
 */
enum PlayCardResult {
    SUCCESS,
    ERROR_NO_ADJACENT_CARDS,
    ERROR_COORDINATES_ALREADY_OCCUPIED,
    ERROR_CANT_PLACE_ON_HIDDEN_CORNER,
    ERROR_NOT_ENOUGH_RESOURCES
}
