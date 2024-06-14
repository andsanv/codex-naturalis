package it.polimi.ingsw.model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.observer.Observer;
import it.polimi.ingsw.model.card.StarterCard;
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
 * Implementation of DeckCreator for StarterCard cards.
 *
 * @see Deck
 * @see StarterCard
 */
public class StarterDeckCreator {
  /**
   * Path to json file containing all StarterCard cards
   */
  private static final Path path = Paths.get("src/main/resources/json/starterCards.json");

  /**
   * Creates and returns a StarterCard cards deck.
   *
   * @return the deck created
   */
  public static Deck<StarterCard> createDeck(List<Observer> observers, AtomicInteger lastEventId) {
    try {
      String content = new String(Files.readAllBytes(path));

      JsonArray jsonArray = JsonParser.parseString(content).getAsJsonArray();

      List<StarterCard> cards = new ArrayList<>();

      Gson gson = new Gson();

      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonObject()) {

          JsonObject jsonObject = jsonElement.getAsJsonObject();

          // id parsing
          int id = jsonObject.get("id").getAsInt();

          // front corner items map parsing
          JsonObject frontCornerItemsObj = jsonObject.getAsJsonObject("frontCornerItems");
          Type cornerItemsType = new TypeToken<Map<String, String>>() {}.getType();
          Map<String, String> frontCornerItemsString =
              gson.fromJson(frontCornerItemsObj, cornerItemsType);
          Map<CornerPosition, Corner> frontCornerItems = new HashMap<>();

          for (Map.Entry<String, String> entry : frontCornerItemsString.entrySet()) {
            List<String> resourceString =
                Arrays.stream(Resources.values()).map(Enum::name).collect(Collectors.toList());

            Corner corner = null;
            // if the value is equal to a Resource value then create a new Corner with resource
            if (resourceString.contains(entry.getValue().toUpperCase())) {
              corner =
                  new Corner(
                      Resources.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);
            } // corner is hidden
            else if ("hidden".equals(entry.getValue())) {
              corner = new Corner(null, CornerTypes.HIDDEN);
            }
            // corner is empty
            else if ("empty".equals(entry.getValue())) {
              corner = new Corner(null, CornerTypes.VISIBLE);
            }

            frontCornerItems.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
          }

          // central items
          JsonObject centralItemsObj = jsonObject.getAsJsonObject("frontCentralItems");
          Set<Resources> centralItems = new HashSet<>();
          Type centralItemType = new TypeToken<Map<String, Integer>>() {}.getType();
          Map<String, Integer> centralItemsMap = gson.fromJson(centralItemsObj, centralItemType);

          for (Map.Entry<String, Integer> entry : centralItemsMap.entrySet()) {
            if (entry.getValue() == 1)
              centralItems.add(Resources.valueOf(entry.getKey().toUpperCase()));
          }

          // back corner items parsing
          JsonObject backCornerItemsObj = jsonObject.getAsJsonObject("backCornerItems");

          Map<String, String> backCornerItemsString =
              gson.fromJson(backCornerItemsObj, cornerItemsType);
          Map<CornerPosition, Corner> backCornerItems = new HashMap<>();

          for (Map.Entry<String, String> entry : backCornerItemsString.entrySet()) {
            Corner corner = null;

            // corner can only be visible with resource
            corner =
                new Corner(Resources.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);

            backCornerItems.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
          }

          cards.add(new StarterCard(id, centralItems, frontCornerItems, backCornerItems));
        }
      }

      return new Deck<>(cards, observers, lastEventId);
    } catch (IOException exception) {
      exception.printStackTrace();
      System.out.println("Creation of starter deck failed!!");
      System.exit(1);
    }

    return null;
  }
}
