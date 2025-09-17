package model.deck;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controller.observer.Observer;
import model.card.StarterCard;
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
 * Implementation of DeckCreator for StarterCard cards.
 *
 * @see Deck
 * @see StarterCard
 */
public class StarterDeckCreator {
  /**
   * Path to json file containing all StarterCard cards
   */
  private static final Path path = Paths.get("/json/starterCards.json");

  /**
   * Creates and returns a StarterCard cards deck.
   *
   * @return the deck created
   */
  public static Deck<StarterCard> createDeck(List<Observer> observers, AtomicInteger lastEventId) {
    try {
      InputStream inputStream = StarterDeckCreator.class.getResourceAsStream(path.toString());

      if (inputStream == null) {
        System.out.println("Starter deck file not found!!");
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

      List<StarterCard> cards = new ArrayList<>();

      Gson gson = new Gson();

      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonObject()) {

          JsonObject jsonObject = jsonElement.getAsJsonObject();

          // id parsing
          int id = jsonObject.get("id").getAsInt();

          // front corner items map parsing
          JsonObject frontCornerItemsObj = jsonObject.getAsJsonObject("frontCornerItems");
          Type cornerItemsType = new TypeToken<Map<String, String>>() {
          }.getType();
          Map<String, String> frontCornerItemsString = gson.fromJson(frontCornerItemsObj, cornerItemsType);
          Map<CornerPosition, Corner> frontCornerItems = new HashMap<>();

          for (Map.Entry<String, String> entry : frontCornerItemsString.entrySet()) {
            List<String> resourceString = Arrays.stream(Resources.values()).map(Enum::name)
                .collect(Collectors.toList());

            Corner corner = null;
            // if the value is equal to a Resource value then create a new Corner with
            // resource
            if (resourceString.contains(entry.getValue().toUpperCase())) {
              corner = new Corner(
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
          Type centralItemType = new TypeToken<Map<String, Integer>>() {
          }.getType();
          Map<String, Integer> centralItemsMap = gson.fromJson(centralItemsObj, centralItemType);

          for (Map.Entry<String, Integer> entry : centralItemsMap.entrySet()) {
            if (entry.getValue() == 1)
              centralItems.add(Resources.valueOf(entry.getKey().toUpperCase()));
          }

          // back corner items parsing
          JsonObject backCornerItemsObj = jsonObject.getAsJsonObject("backCornerItems");

          Map<String, String> backCornerItemsString = gson.fromJson(backCornerItemsObj, cornerItemsType);
          Map<CornerPosition, Corner> backCornerItems = new HashMap<>();

          for (Map.Entry<String, String> entry : backCornerItemsString.entrySet()) {
            Corner corner = null;

            // corner can only be visible with resource
            corner = new Corner(Resources.valueOf(entry.getValue().toUpperCase()), CornerTypes.VISIBLE);

            backCornerItems.put(CornerPosition.valueOf(entry.getKey().toUpperCase()), corner);
          }

          cards.add(new StarterCard(id, centralItems, frontCornerItems, backCornerItems));
        }
      }

      inputStream.close();
      bufferedReader.close();

      return new Deck<>(cards, observers, lastEventId);
    } catch (IOException exception) {
      exception.printStackTrace();
      System.out.println("Creation of starter deck failed!!");
      System.exit(1);
    }

    return null;
  }
}
