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
  void addTest() {
    int size = playerHand.getCards().size();

    playerHand.add(card3);

    assertEquals(size + 1, playerHand.getCards().size());
    assertTrue(playerHand.getCards().contains(card3));
  }

  @Test
  void removeTest() {
    playerHand.add(card3);

    int size = playerHand.getCards().size();

    playerHand.remove(card3);

    assertEquals(size - 1, playerHand.getCards().size());
    assertFalse(playerHand.getCards().contains(card3));
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
