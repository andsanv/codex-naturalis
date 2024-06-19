package it.polimi.ingsw.view.gui.cache;


import java.util.ArrayList;
import java.util.List;

public class ActionCache {
    private List<PlayCardAction> cache;

    public ActionCache() {
        this.cache = new ArrayList<>();
    }

    public void addAction(PlayCardAction action) {
        cache.add(action);
    }

    public PlayCardAction getAction(int index) {
        return cache.get(index);
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public int size() {
        return cache.size();
    }
}
