package it.polimi.ingsw.model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.PointsType;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.corner.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of DeckCreator for GoldCard cards.
 *
 * @see Deck
 * @see GoldCard
 */
public class GoldDeckCreator implements DeckCreator {
  /**
   * Path to json file containing all GoldCard cards
   */
  private static final Path path = Paths.get("src/main/resources/json/goldCards.json");

  /**
   * Creates and returns a GoldCard cards deck.
   *
   * @return the deck created
   */
  public static Deck<GoldCard> createDeck(List<Observer> observers, AtomicInteger lastEventId) {
    try {
      String content = new String(Files.readAllBytes(path));

      JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

      List<GoldCard> cards = new ArrayList<>();

      Gson gson = new Gson();

      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonObject()) {

          JsonObject jsonObject = jsonElement.getAsJsonObject();

          // id parsing
          int id = jsonObject.get("id").getAsInt();

          // resource type parsing
          String resourceTypeString = jsonObject.get("resourceType").getAsString();
          Resources resourceType = Resources.valueOf(resourceTypeString.toUpperCase());

          // points type parsing
          String pointsType = jsonObject.get("pointsType").getAsString();
          PointsType goldCardPoint = PointsType.valueOf(pointsType.toUpperCase());

          // resource needed map parsing
          JsonObject resourcesNeededObj = jsonObject.getAsJsonObject("resourcesNeeded");
          Type resourceMapType = new TypeToken<Map<String, Integer>>() {}.getType();
          Map<String, Integer> requiredResourcesString =
              gson.fromJson(resourcesNeededObj, resourceMapType);
          Map<Resources, Integer> requiredResources = new HashMap<>();

          for (Map.Entry<String, Integer> entry : requiredResourcesString.entrySet()) {
            requiredResources.put(
                Resources.valueOf(entry.getKey().toUpperCase()), entry.getValue());
          }

          // corner items map parsing
          JsonObject cornerItemsObj = jsonObject.getAsJsonObject("cornerItems");
          Type cornerItemsType = new TypeToken<Map<String, String>>() {}.getType();
          Map<String, String> cornerItemsString = gson.fromJson(cornerItemsObj, cornerItemsType);
          Map<CornerPosition, Corner> frontCorners = new HashMap<>();
          Map<CornerPosition, Corner> backCorners = new HashMap<>();

          for (Map.Entry<String, String> entry : cornerItemsString.entrySet()) {

            List<String> resourceString =
                Arrays.stream(Resources.values()).map(Enum::name).collect(Collectors.toList());
            List<String> itemsString =
                Arrays.stream(Items.values()).map(Enum::name).collect(Collectors.toList());

            Corner corner = null;
            // if the value is equal to a Resource value then create a new Corner with resource
            if (resourceString.contains(entry.getValue().toUpperCase())) {
              corner =
                  new Corner(
                      Resources.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);
            } // else if the value is equal to an Items value then create a new Corner with item
            else if (itemsString.contains(entry.getValue().toUpperCase())) {
              corner =
                  new Corner(Items.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);
            } // corner is hidden
            else if ("hidden".equals(entry.getValue())) {
              corner = new Corner(null, CornerTypes.HIDDEN);
            } // corner is empty
            else if ("empty".equals(entry.getValue())) {
              corner = new Corner(null, CornerTypes.VISIBLE);
            }

            frontCorners.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);

            backCorners.put(
                CornerPosition.valueOf(entry.getKey().toUpperCase()),
                new Corner(null, CornerTypes.VISIBLE));
          }

          cards.add(
              new GoldCard(id, frontCorners, backCorners,resourceType, goldCardPoint, requiredResources));
        }
      }

      return new Deck<>(cards, observers, lastEventId);
    } catch (IOException exception) {
      exception.printStackTrace();
      System.out.println("Creation of gold deck failed!!");
      System.exit(1);
    }

    return null;
  }
}
