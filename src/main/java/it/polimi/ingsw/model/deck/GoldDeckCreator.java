package it.polimi.ingsw.model.deck;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.corner.*;
import com.google.gson.*;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.card.GoldCardPoints;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GoldDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/goldCards.json");

    @Override
    public Deck<GoldCard> createDeck() throws IOException {
        String content = new String(Files.readAllBytes(path));

        JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

        List<GoldCard> cards = new ArrayList<>();

        Gson gson = new Gson();

        for(JsonElement jsonElement: jsonArray) {
            if(jsonElement.isJsonObject()) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // id parsing
                int id = jsonObject.get("id").getAsInt();


                // resource type parsing
                String resourceTypeString = jsonObject.get("resourceType").getAsString();
                Resources resourceType = Resources.valueOf(resourceTypeString.toUpperCase());


                // points type parsing
                String pointsType = jsonObject.get("pointsType").getAsString();
                GoldCardPoints goldCardPoint = GoldCardPoints.valueOf(pointsType.toUpperCase());

                // resource needed map parsing
                JsonObject resourcesNeededObj = jsonObject.getAsJsonObject("resourcesNeeded");
                Type resourceMapType = new TypeToken<Map<String, Integer>>() {}.getType();
                Map<String, Integer> requiredResourcesString = gson.fromJson(resourcesNeededObj, resourceMapType);
                Map<Resources, Integer> requiredResources = new HashMap<>();

                for (Map.Entry<String, Integer> entry : requiredResourcesString.entrySet()) {
                    requiredResources.put(Resources.valueOf(entry.getKey().toUpperCase()), entry.getValue());
                }


                // corner items map parsing
                JsonObject cornerItemsObj = jsonObject.getAsJsonObject("cornerItems");
                Type cornerItemsType = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> cornerItemsString = gson.fromJson(cornerItemsObj, cornerItemsType);
                Map<CornerPosition, Corner> frontCorners = new HashMap<>();
                Map<CornerPosition, Corner> backCorner = new HashMap<>();

                for (Map.Entry<String, String> entry : cornerItemsString.entrySet()) {
                    for(Items item: Items.values())
                        ;

                    Arrays.asList(Items.values()).stream().map(i -> i.name()).

                    //corner = new VisibleCorner(CornerItems.valueOf(entry.getValue().toUpperCase()));


                    frontCorners.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
                    backCorner.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), new VisibleCorner(CornerItems.EMPTY));
                }

                cards.add(new GoldCard(id, resourceType, goldCardPoint, requiredResources, frontCorners, backCorner));
            }
        }

        return new Deck<>(cards);
    }
}
