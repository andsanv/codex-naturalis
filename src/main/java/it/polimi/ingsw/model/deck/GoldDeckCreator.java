package it.polimi.ingsw.model.deck;

import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.corner.*;
import com.google.gson.*;

import it.polimi.ingsw.model.card.Resources;
import it.polimi.ingsw.model.card.GoldCardPoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/goldCards.json");

    @Override
    public Deck<GoldCard> createDeck() throws IOException {
        String content = new String(Files.readAllBytes(path));

        JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

        List<GoldCard> cards = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // get id
            int id = jsonObject.getInt("cardNum");

            // get resource type
            String resourceTypeString = jsonObject.getString("resourceType");
            Resources resourceType = null;
            switch (resourceTypeString) {
                case "f": resourceType = Resources.FUNGI; break;
                case "p": resourceType = Resources.PLANT; break;
                case "a": resourceType = Resources.ANIMAL; break;
                case "i": resourceType = Resources.INSECT; break;
            }

            // get points given
            String goldCardPointsString = jsonObject.getString("pointsType");
            GoldCardPoints goldCardPoints = null;
            switch (goldCardPointsString) {
                case "1": goldCardPoints = GoldCardPoints.ONE; break;
                case "3": goldCardPoints = GoldCardPoints.THREE; break;
                case "5": goldCardPoints = GoldCardPoints.FIVE; break;
                case "w": goldCardPoints = GoldCardPoints.ONE_PER_INKWELL; break;
                case "q": goldCardPoints = GoldCardPoints.ONE_PER_QUILL; break;
                case "m": goldCardPoints = GoldCardPoints.ONE_PER_MANUSCRIPT; break;
                case "c": goldCardPoints = GoldCardPoints.TWO_PER_COVERED_CORNER; break;
            }

            String resourcesNeededString = jsonObject.getString("resourcesNeeded");
            int counters[] = new int[]{0, 0, 0, 0};
            for(int j = 0; j < resourcesNeededString.length(); j++) {

                Character character = resourcesNeededString.charAt(j);
                switch (character) {
                    case 'f': counters[Resources.FUNGI.ordinal()]++; break;
                    case 'a': counters[Resources.ANIMAL.ordinal()]++; break;
                    case 'i': counters[Resources.INSECT.ordinal()]++; break;
                    case 'p': counters[Resources.PLANT.ordinal()]++; break;
                }
            }

            Map<Resources, Integer> requiredResources = new HashMap<>();
            requiredResources.put(Resources.FUNGI, counters[Resources.FUNGI.ordinal()]);
            requiredResources.put(Resources.ANIMAL, counters[Resources.ANIMAL.ordinal()]);
            requiredResources.put(Resources.INSECT, counters[Resources.INSECT.ordinal()]);
            requiredResources.put(Resources.PLANT, counters[Resources.PLANT.ordinal()]);


            String cornerItemsString = jsonObject.getString("cornerItems");
            Map<CornerPosition, Corner> frontCorners = new HashMap<>();
            Map<CornerPosition, Corner> backCorner = new HashMap<>();

            for(CornerPosition cornerPosition: CornerPosition.values()) {
                Corner corner = null;
                switch (cornerItemsString.charAt(cornerPosition.ordinal())) {
                    case '-': corner = new HiddenCorner(); break;
                    case 'x': corner = new VisibleCorner(CornerItems.EMPTY); break;
                    case 'q': corner = new VisibleCorner(CornerItems.QUILL); break;
                    case 'w': corner = new VisibleCorner(CornerItems.INKWELL); break;
                    case 'm': corner = new VisibleCorner(CornerItems.MANUSCRIPT); break;
                    case 'a': corner = new VisibleCorner(CornerItems.ANIMAL); break;
                    case 'p': corner = new VisibleCorner(CornerItems.PLANT); break;
                    case 'f': corner = new VisibleCorner(CornerItems.FUNGI); break;
                    case 'i': corner = new VisibleCorner(CornerItems.INSECT); break;
                }

                frontCorners.put(cornerPosition, corner);
                backCorner.put(cornerPosition, new VisibleCorner(CornerItems.EMPTY));

            }
            cards.add(new GoldCard(id, resourceType, goldCardPoints, requiredResources, frontCorners, backCorner));

        }


        // TODO implement json import
        return new Deck<>(cards);
    }
}
