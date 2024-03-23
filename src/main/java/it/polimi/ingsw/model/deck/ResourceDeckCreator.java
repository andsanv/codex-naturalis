package it.polimi.ingsw.model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.corner.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/resourceCards.json");

    @Override
    public Deck<ResourceCard> createDeck() throws IOException {
        String content = new String(Files.readAllBytes(path));

        JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

        List<ResourceCard> cards = new ArrayList<>();

        Gson gson = new Gson();

        for(JsonElement jsonElement: jsonArray) {
            if(jsonElement.isJsonObject()) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // id parsing
                int id = jsonObject.get("id").getAsInt();


                // resource type parsing
                String resourceTypeString = jsonObject.get("resourceType").getAsString();
                Resources resourceType = Resources.valueOf(resourceTypeString.toUpperCase());


                // corner items map parsing
                JsonObject cornerItemsObj = jsonObject.getAsJsonObject("cornerItems");
                Type cornerItemsType = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> cornerItemsString = gson.fromJson(cornerItemsObj, cornerItemsType);
                Map<CornerPosition, Corner> frontCorners = new HashMap<>();
                Map<CornerPosition, Corner> backCorner = new HashMap<>();

                for (Map.Entry<String, String> entry : cornerItemsString.entrySet()) {
                    Corner corner = null;
                    if(entry.getValue().equals("hidden"))
                        corner = new HiddenCorner();
                    else
                        corner = new VisibleCorner(CornerItems.valueOf(entry.getValue().toUpperCase()));

                    frontCorners.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
                    backCorner.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), new VisibleCorner(CornerItems.EMPTY));
                }


                // points amount parsing
                int points = jsonObject.get("pointsAmount").getAsInt();
                ResourceCardPoints resourceCardPoints = points == 0 ? ResourceCardPoints.ZERO : ResourceCardPoints.ONE;

                cards.add(new ResourceCard(id, resourceType, resourceCardPoints, frontCorners, backCorner));
            }
        }

        return new Deck<>(cards);
    }
}