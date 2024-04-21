package it.polimi.ingsw.model.deck;

import com.google.gson.*;
import it.polimi.ingsw.model.card.ObjectiveCard;
import it.polimi.ingsw.model.card.objective.ItemsObjectiveStrategy;
import it.polimi.ingsw.model.card.objective.PatternObjectiveStrategy;
import it.polimi.ingsw.model.common.Elements;
import it.polimi.ingsw.model.common.Items;
import it.polimi.ingsw.model.common.Resources;
import it.polimi.ingsw.model.player.Coords;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ObjectiveDeckCreator implements DeckCreator {
    static final private Path path = Paths.get("src/main/resources/json/objectiveCards.json");

    public static Deck<ObjectiveCard> createDeck() {
        try {
            String content = new String(Files.readAllBytes(path));

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
                        if (Arrays.asList(new Character[]{'A', 'I', 'P', 'F'}).contains(character)) {
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
                                        requiredItems.put(Items.INKWELL, requiredItems.containsKey(Items.INKWELL) ?
                                                requiredItems.put(Items.INKWELL, requiredItems.get(Items.INKWELL) + 1) : Integer.valueOf(1));
                                        break;
                                    case 'Q':
                                        requiredItems.put(Items.QUILL, requiredItems.containsKey(Items.QUILL) ?
                                                requiredItems.put(Items.QUILL, requiredItems.get(Items.QUILL) + 1) : Integer.valueOf(1));
                                        break;
                                    case 'M':
                                        requiredItems.put(Items.MANUSCRIPT, requiredItems.containsKey(Items.MANUSCRIPT) ?
                                                requiredItems.put(Items.MANUSCRIPT, requiredItems.get(Items.MANUSCRIPT) + 1) : Integer.valueOf(1));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    if (requiredItems.isEmpty())
                        cards.add(new ObjectiveCard(id, points, new PatternObjectiveStrategy(patternMap)));
                    else
                        cards.add(new ObjectiveCard(id, points, new ItemsObjectiveStrategy(requiredItems)));
                }
            }

            return new Deck<>(cards);
        } catch (IOException exception) {
            System.out.println("Creation of objective deck failed!!");
            System.exit(1);
        }

        return null;
    }
}
