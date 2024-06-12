package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.model.player.PlayerToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScoreTrackTest {
  private ScoreTrack scoreTrack;
  private List<PlayerToken> playerTokens;

  @BeforeEach
  void init() {
    playerTokens =
        new ArrayList<>(
            Arrays.asList(
                PlayerToken.RED, PlayerToken.GREEN, PlayerToken.BLUE, PlayerToken.YELLOW));

    scoreTrack = new ScoreTrack(playerTokens);
  }

  @Test
  void testGetScore() {
    for (PlayerToken playerToken : playerTokens) {
      assertEquals(0, scoreTrack.getScores().get(playerToken).intValue());
    }
  }

  @Test
  void testUpdatePlayerScore() {
    int score1 = 10;
    int score2 = 3;

    scoreTrack.updatePlayerScore(PlayerToken.RED, score1);
    assertEquals(score1, scoreTrack.getScores().get(PlayerToken.RED));

    scoreTrack.updatePlayerScore(PlayerToken.GREEN, score2);
    assertEquals(score2, scoreTrack.getScores().get(PlayerToken.GREEN));

    int score1_increment = 3;

    scoreTrack.updatePlayerScore(PlayerToken.RED, score1_increment);
    assertEquals(score1 + score1_increment, scoreTrack.getScores().get(PlayerToken.RED));
    assertEquals(score2, scoreTrack.getScores().get(PlayerToken.GREEN));
  }

  @Test
  void testGameEnded() {
    scoreTrack.updatePlayerScore(PlayerToken.RED, 19);
    scoreTrack.updatePlayerScore(PlayerToken.GREEN, 5);

    assertFalse(scoreTrack.limitPointsReached());

    scoreTrack.updatePlayerScore(PlayerToken.RED, 1);

    assertTrue(scoreTrack.limitPointsReached());
  }
}
