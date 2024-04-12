package it.polimi.ingsw.model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.card.*;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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


                    List<String> resourceString = Arrays.stream(Resources.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                    List<String> itemsString = Arrays.stream(Items.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());

                    Corner corner = null;
                    // if the value is equal to a Resource value then create a new Corner with resource
                    if(resourceString.contains(entry.getValue().toUpperCase())) {
                        corner = new Corner(Resources.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);
                    } // else if the value is equal to an Items value then create a new Corner with item
                    else if(itemsString.contains(entry.getValue().toUpperCase())) {
                        corner = new Corner(Items.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);
                    } // corner is hidden
                    else if("hidden".equals(entry.getValue())){
                        corner = new Corner(null, CornerTypes.HIDDEN);
                    } // corner is empty
                    else if("empty".equals(entry.getValue())){
                        corner = new Corner(null, CornerTypes.VISIBLE);
                    }

                    frontCorners.put(CornerPosition.valueOf(entry.getKey().toUpperCase()),
                            corner);

                    backCorner.put(CornerPosition.valueOf(entry.getKey().toUpperCase()),
                            new Corner(null, CornerTypes.VISIBLE));

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