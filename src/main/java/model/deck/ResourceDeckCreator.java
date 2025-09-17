package model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controller.observer.Observer;
import model.card.*;
import model.common.Items;
import model.common.Resources;
import model.corner.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of DeckCreator for ResourceCard cards.
 *
 * @see Deck
 * @see ResourceCard
 */
public class ResourceDeckCreator {
  /**
   * Path to json file containing all ResourceCard cards
   */
  private static final Path path = Paths.get("/json/resourceCards.json");

  /**
   * Creates and returns a ResourceCard cards deck.
   *
   * @return the deck created
   */
  public static Deck<ResourceCard> createDeck(List<Observer> observers, AtomicInteger lastEventId) {
    try {
      InputStream inputStream = ResourceDeckCreator.class.getResourceAsStream(path.toString());

      if (inputStream == null) {
        System.out.println("Resource deck file not found!!");
        System.exit(1);
      }

      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder stringBuilder = new StringBuilder();
      String line;

      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }

      String content = stringBuilder.toString();

      JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

      List<ResourceCard> cards = new ArrayList<>();

      Gson gson = new Gson();

      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonObject()) {

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

            backCorner.put(
                CornerPosition.valueOf(entry.getKey().toUpperCase()),
                new Corner(null, CornerTypes.VISIBLE));
          }

          // points amount parsing
          int points = jsonObject.get("pointsAmount").getAsInt();
          PointsType resourceCardPoints = points == 0 ? PointsType.ZERO : PointsType.ONE;

          cards.add(
              new ResourceCard(id, resourceType, resourceCardPoints, frontCorners, backCorner));
        }
      }

      inputStream.close();
      bufferedReader.close();

      return new Deck<>(cards, observers, lastEventId);
    } catch (IOException exception) {
      exception.printStackTrace();
      System.out.println("Creation of resource deck failed!!");
      System.exit(1);
    }

    return null;
  }
}
