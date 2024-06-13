package it.polimi.ingsw.model.player;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.card.GoldCard;
import it.polimi.ingsw.model.card.PlayableCard;
import it.polimi.ingsw.model.card.ResourceCard;
import java.util.ArrayList;
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

    cards = new ArrayList<PlayableCard>();

    playerHand = new PlayerHand();

    playerHand.addCard(card1);
    playerHand.addCard(card2);
  }

  @Test
  void addCard() {
    int size = playerHand.getCards().size();

    playerHand.addCard(card3);

    assertEquals(size + 1, playerHand.getCards().size());
    assertTrue(playerHand.getCards().contains(card3));
  }

  @Test
  void removeCard() {
    playerHand.addCard(card3);

    int size = playerHand.getCards().size();

    playerHand.removeCard(card3);

    assertEquals(size - 1, playerHand.getCards().size());
    assertFalse(playerHand.getCards().contains(card3));
  }

  @Test
  void getCards() {
    playerHand.addCard(card3);

    List<PlayableCard> cardsTest = new ArrayList<PlayableCard>();

    cardsTest.add(card1);
    cardsTest.add(card2);
    cardsTest.add(card3);

    assertTrue(playerHand.getCards().containsAll(cardsTest));
    assertTrue(cardsTest.containsAll(playerHand.getCards()));
  }
}
