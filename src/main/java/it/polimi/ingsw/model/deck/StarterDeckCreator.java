package it.polimi.ingsw.model.deck;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.card.StarterCard;
import com.google.gson.*;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.corner.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StarterDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/starterCards.json");

    @Override
    public Deck<StarterCard> createDeck() throws IOException {
        String content = new String(Files.readAllBytes(path));

        JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

        List<StarterCard> cards = new ArrayList<>();

        Gson gson = new Gson();

        for(JsonElement jsonElement: jsonArray) {
            if (jsonElement.isJsonObject()) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // id parsing
                int id = jsonObject.get("id").getAsInt();


                // front corner items map parsing
                JsonObject frontCornerItemsObj = jsonObject.getAsJsonObject("frontCornerItems");
                Type cornerItemsType = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> frontCornerItemsString = gson.fromJson(frontCornerItemsObj, cornerItemsType);
                Map<CornerPosition, Corner> frontCornerItems = new HashMap<>();

                for (Map.Entry<String, String> entry : frontCornerItemsString.entrySet()) {
                    Corner corner = null;
                    if(entry.getValue().equals("hidden"))
                        corner = new HiddenCorner();
                    else
                        corner = new VisibleCorner(CornerItems.valueOf(entry.getValue().toUpperCase()));

                    frontCornerItems.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
                }


                // central items
                JsonObject centralItemsObj = jsonObject.getAsJsonObject("frontCentralItems");
                Set<Resources> centralItems = new HashSet<>();
                Type centralItemType = new TypeToken<Map<String, Integer>>() {}.getType();
                Map<String, Integer> centralItemsMap = gson.fromJson(centralItemsObj, centralItemType);

                for(Map.Entry<String, Integer> entry : centralItemsMap.entrySet()) {
                    if(entry.getValue() == 1)
                        centralItems.add(Resources.valueOf(entry.getKey().toUpperCase()));
                }


                // back corner items parsing
                JsonObject backCornerItemsObj = jsonObject.getAsJsonObject("backCornerItems");

                Map<String, String> backCornerItemsString = gson.fromJson(frontCornerItemsObj, cornerItemsType);
                Map<CornerPosition, Corner> backCornerItems = new HashMap<>();

                for (Map.Entry<String, String> entry : backCornerItemsString.entrySet()) {
                    Corner corner = null;
                    if(entry.getValue().equals("hidden"))
                        corner = new HiddenCorner();
                    else
                        corner = new VisibleCorner(CornerItems.valueOf(entry.getValue().toUpperCase()));

                    backCornerItems.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
                }


                cards.add(new StarterCard(id, centralItems, frontCornerItems, backCornerItems));

            }
        }

        // TODO implement json import
        return new Deck<>(cards);
    }

}
