package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.model.player.PlayerToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ScoreTrackTest {
  private ScoreTrack scoreTrack;
  private Map<PlayerToken, Integer> initialScores;

  @BeforeEach
  void setUp() {
    initialScores = new HashMap<>() {{
      put(PlayerToken.RED, 0);
      put(PlayerToken.GREEN, 0);
      put(PlayerToken.BLUE, 0);
      put(PlayerToken.YELLOW, 0);
    }};

    scoreTrack = new ScoreTrack(initialScores, new ArrayList<>(), new AtomicInteger(0));
  }

  @Test
  void updatePlayerScoreTest() {
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
  void limitPointsReachedTest() {
    scoreTrack.updatePlayerScore(PlayerToken.RED, 10);
    assertFalse(scoreTrack.limitPointsReached());
  
    scoreTrack.updatePlayerScore(PlayerToken.GREEN, 21);
    assertTrue(scoreTrack.limitPointsReached());
  }

  @Test
  void getScoresTest() {
    initialScores.keySet().forEach(
      x -> assertEquals(0, scoreTrack.getScores().get(x))
    );

    scoreTrack.updatePlayerScore(PlayerToken.YELLOW, 4);
    assertEquals(4, scoreTrack.getScores().get(PlayerToken.YELLOW));
  }
}
