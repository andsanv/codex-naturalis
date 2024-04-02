package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.ObjectiveCard;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ObjectiveDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/objectiveCards.json");

    @Override
    public Deck<ObjectiveCard> createDeck() {

        // TODO implement json import
        return null;
    }
}
