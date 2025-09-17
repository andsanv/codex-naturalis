package model.deck;

import com.google.gson.*;
import controller.observer.Observer;
import model.card.ObjectiveCard;
import model.card.objective.ItemsObjectiveStrategy;
import model.card.objective.PatternObjectiveStrategy;
import model.common.Elements;
import model.common.Items;
import model.common.Resources;
import model.player.Coords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of DeckCreator for ObjectiveCard cards.
 *
 * @see Deck
 * @see ObjectiveCard
 */
public class ObjectiveDeckCreator {
  /**
   * Path to json file containing all ObjectiveCard cards
   */
  private static final Path path = Paths.get("/json/objectiveCards.json");

  /**
   * Creates and returns an ObjectiveCard cards deck.
   *
   * @return the deck created
   */
  public static Deck<ObjectiveCard> createDeck(List<Observer> observers, AtomicInteger lastEventId) {
    try {
      InputStream inputStream = ObjectiveDeckCreator.class.getResourceAsStream(path.toString());

      if (inputStream == null) {
        System.out.println("Objective deck file not found!!");
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

      List<ObjectiveCard> cards = new ArrayList<>();

      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonObject()) {

          JsonObject jsonObject = jsonElement.getAsJsonObject();

          // id parsing
          int id = jsonObject.get("id").getAsInt();

          // points parsing
          int points = jsonObject.get("points").getAsInt();

          // pattern parsing
          String resourceTypeString = jsonObject.get("pattern").getAsString();
          Map<Coords, Resources> patternMap = new HashMap<>();
          Map<Elements, Integer> requiredItems = new HashMap<>();

          if (resourceTypeString.length() == 9) {
            for (int i = 0; i < resourceTypeString.length(); i++) {
              switch (resourceTypeString.charAt(i)) {
                case 'I':
                  patternMap.put(new Coords(i / 3, i % 3), Resources.INSECT);
                  break;
                case 'A':
                  patternMap.put(new Coords(i / 3, i % 3), Resources.ANIMAL);
                  break;
                case 'F':
                  patternMap.put(new Coords(i / 3, i % 3), Resources.FUNGI);
                  break;
                case 'P':
                  patternMap.put(new Coords(i / 3, i % 3), Resources.PLANT);
                  break;
                default:
                  break;
              }
            }
          } else if (resourceTypeString.length() == 8) {
            for (int i = 0; i < resourceTypeString.length(); i++) {
              switch (resourceTypeString.charAt(i)) {
                case 'I':
                  patternMap.put(new Coords(i / 4, i % 4), Resources.INSECT);
                  break;
                case 'A':
                  patternMap.put(new Coords(i / 4, i % 4), Resources.ANIMAL);
                  break;
                case 'F':
                  patternMap.put(new Coords(i / 4, i % 4), Resources.FUNGI);
                  break;
                case 'P':
                  patternMap.put(new Coords(i / 4, i % 4), Resources.PLANT);
                  break;
                default:
                  break;
              }
            }
          } else {
            Character character = resourceTypeString.charAt(0);

            // resources beginning letters
            if (Arrays.asList(new Character[] {'A', 'I', 'P', 'F'}).contains(character)) {
              switch (character) {
                case 'I':
                  requiredItems.put(Resources.INSECT, 3);
                  break;
                case 'A':
                  requiredItems.put(Resources.ANIMAL, 3);
                  break;
                case 'F':
                  requiredItems.put(Resources.FUNGI, 3);
                  break;
                case 'P':
                  requiredItems.put(Resources.PLANT, 3);
                  break;
              }
            } else {
              for (int i = 0; i < resourceTypeString.length(); i++) {
                switch (resourceTypeString.charAt(i)) {
                  case 'W':
                    requiredItems.put(
                        Items.INKWELL,
                        requiredItems.containsKey(Items.INKWELL)
                            ? requiredItems.get(Items.INKWELL) + 1
                            : 1);
                    break;
                  case 'Q':
                    requiredItems.put(
                        Items.QUILL,
                        requiredItems.containsKey(Items.QUILL)
                            ? requiredItems.get(Items.QUILL) + 1
                            : 1);
                    break;
                  case 'M':
                    requiredItems.put(
                        Items.MANUSCRIPT,
                        requiredItems.containsKey(Items.MANUSCRIPT)
                            ? requiredItems.get(Items.MANUSCRIPT) + 1
                            : 1);
                    break;
                  default:
                    break;
                }
              }
            }
          }

          if (requiredItems.isEmpty())
            cards.add(new ObjectiveCard(id, points, new PatternObjectiveStrategy(patternMap)));
          else cards.add(new ObjectiveCard(id, points, new ItemsObjectiveStrategy(requiredItems)));
        }
      }

      inputStream.close();
      bufferedReader.close();

      return new Deck<>(cards, observers, lastEventId);
    } catch (IOException exception) {
      exception.printStackTrace();
      System.out.println("Creation of objective deck failed!!");
      System.exit(1);
    }

    return null;
  }
}
