package it.polimi.ingsw.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.ResourceCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerHandTest {

  private List<PlayableCard> cards;
  private PlayerHand playerHand;

  @Mock private PlayableCard card1;

  @Mock private PlayableCard card2;

  @Mock private PlayableCard card3;

  @BeforeEach
  void setUp() {
    card1 = Mockito.mock(ResourceCard.class);
    card2 = Mockito.mock(ResourceCard.class);
    card3 = Mockito.mock(GoldCard.class);
  }

  @Test
  void constructorTest() {
    try {
      new PlayerHand(Arrays.asList(card1, card2));
      assertFalse(true);
    } catch (Exception e) {}

    try {
      new PlayerHand(Arrays.asList(card1, card2, card3, card1));
      assertFalse(true);
    } catch (Exception e) {}
    
    try {
      new PlayerHand(Arrays.asList(card1));
      assertFalse(true);
    } catch (Exception e) {}

    try {
      new PlayerHand(Arrays.asList(null, null, null));
    }
    catch(Exception e) {
      assertFalse(true);
    }
  }

  @Test
  void addTest() {
    cards = new ArrayList<>(Arrays.asList(card1, card2, card3));
    playerHand = new PlayerHand(cards);

    playerHand.remove(card1);
    playerHand.remove(card2);
    playerHand.remove(card3);

    assertEquals(0, playerHand.add(card1));
    assertEquals(1, playerHand.add(card3));
    assertEquals(2, playerHand.add(card2));

    assertEquals(-1, playerHand.add(card2));
    
    playerHand.remove(card3);
    assertEquals(1, playerHand.add(card1));
  }

  @Test
  void removeTest() {
    cards = new ArrayList<>(Arrays.asList(card1, card2, card3));
    playerHand = new PlayerHand(cards);

    assertTrue(playerHand.remove(card3));
    assertNotNull(playerHand.getCards().get(0));
    assertNotNull(playerHand.getCards().get(1));
    assertNull(playerHand.getCards().get(2));

    assertFalse(playerHand.remove(card3));
    assertNotNull(playerHand.getCards().get(0));
    assertNotNull(playerHand.getCards().get(1));
    assertNull(playerHand.getCards().get(2));

    assertTrue(playerHand.remove(card1));
    assertNull(playerHand.getCards().get(0));
    assertNotNull(playerHand.getCards().get(1));
    assertNull(playerHand.getCards().get(2));
  }

  @Test
  void getTest() {    
    PlayableCard card4 = new ResourceCard(0, null, null, null, null);
    PlayableCard card5 = new GoldCard(1, null, null, null, null, null);
    PlayableCard card6 = new GoldCard(2, null, null, null, null, null);
    playerHand = new PlayerHand(Arrays.asList(card4, card5, card6));

    assertEquals(card4, playerHand.get(card4.id));
    assertEquals(card5, playerHand.get(card5.id));
    assertEquals(card6, playerHand.get(card6.id));
    playerHand.remove(card6);
    assertNull(playerHand.get(card6.id));
  }

  @Test
  void sizeTest() {
    cards = new ArrayList<>(Arrays.asList(card1, card2, card3));
    playerHand = new PlayerHand(cards);

    assertEquals(3, playerHand.size());
    
    playerHand.remove(card1);
    assertEquals(2, playerHand.size());

    playerHand.remove(card3);
    assertEquals(1, playerHand.size());

    playerHand.add(card1);
    assertEquals(2, playerHand.size());

    playerHand.remove(card2);
    assertEquals(1, playerHand.size());

    playerHand.remove(card1);
    assertEquals(0, playerHand.size());

    playerHand.add(card1);
    playerHand.add(card2);
    playerHand.add(card3);
    assertEquals(3, playerHand.size());

  }

  @Test
  void getFirstFreeTest() {
    cards = new ArrayList<>(Arrays.asList(card1, card2, card3));
    playerHand = new PlayerHand(cards);
    assertEquals(-1, playerHand.getFirstFree());

    playerHand.remove(card2);
    assertEquals(1, playerHand.getFirstFree());

    playerHand.remove(card1);
    assertEquals(0, playerHand.getFirstFree());

    playerHand.add(card1);
    playerHand.add(card2);

    assertEquals(-1, playerHand.getFirstFree());
    playerHand.remove(card3);
    assertEquals(2, playerHand.getFirstFree());

    playerHand.remove(card1);
    assertEquals(0, playerHand.getFirstFree());
  }
}
