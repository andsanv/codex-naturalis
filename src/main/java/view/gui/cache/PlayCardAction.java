package view.gui.cache;

import model.card.CardSide;
import model.player.Coords;
import model.player.PlayerToken;

public class PlayCardAction {
    private PlayerToken playerToken;
    private Coords coords;
    private int cardId;
    private CardSide cardSide;

    public PlayCardAction(PlayerToken playerToken, Coords coords, int cardId, CardSide cardSide) {
        this.playerToken = playerToken;
        this.coords = coords;
        this.cardId = cardId;
        this.cardSide = cardSide;
    }


    public PlayerToken getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(PlayerToken playerToken) {
        this.playerToken = playerToken;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public CardSide getCardSide() {
        return cardSide;
    }

    public void setCardSide(CardSide cardSide) {
        this.cardSide = cardSide;
    }
}
