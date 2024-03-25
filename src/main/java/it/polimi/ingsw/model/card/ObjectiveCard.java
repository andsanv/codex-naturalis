package it.polimi.ingsw.model.card;

/**
 * An objective card is a card that gives points to the players when the last turn ends if a certain condition is satisfied.
 * Each player has a personal objective, only visible to him, and all the players share two common objectives, visible by everyone.
 */
public class ObjectiveCard extends Card {
    // TODO maybe add a lambda to compute objective score
    // TODO Create objective class and logic and getter

    private PointCalculator pointCalculator;

    public ObjectiveCard(int id, PointCalculator pointCalculator) {
        super(id);
        this.pointCalculator = pointCalculator;
    }

    public int calculatePoints() {
        return pointCalculator.calculatePoints();
    }
}


//STRATEGY PATTERN

// Interface for the strategy pattern
interface PointCalculator {
    int calculatePoints();
}

// Calculate points based on items on a player board
class ItemsPointCalculator implements PointCalculator {
    //TODO
    @Override
    public int calculatePoints() {
        return 1;
    }
}

// Calculate points based on patterns
class PatternPointCalculator implements PointCalculator {
    //TODO
    @Override
    public int calculatePoints() {
        return 2;
    }
}