package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import it.polimi.ingsw.model.player.Player;

@ExtendWith(MockitoExtension.class)
public class ScoreTrackTest {
    private ScoreTrack scoreTrack;
    private List<Player> players;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @BeforeEach
    void init() {
        player1 = Mockito.mock(Player.class);
        player2 = Mockito.mock(Player.class);

        players = new ArrayList<Player>();

        players.add(player1);
        players.add(player2);
        
        scoreTrack = new ScoreTrack(players);
    }

    @Test
    void testGetScore() {
        for (Player player : players) {
            assertEquals(0, scoreTrack.getScore().get(player).intValue());
        }
    }

    @Test
    void testUpdatePlayerScore() {
        int score1 = 10;
        int score2 = 3;

        scoreTrack.updatePlayerScore(player1, score1);
        assertEquals(score1, scoreTrack.getScore().get(player1));

        scoreTrack.updatePlayerScore(player2, score2);
        assertEquals(score2, scoreTrack.getScore().get(player2));

        int score1_increment = 3;

        scoreTrack.updatePlayerScore(player1, score1_increment);
        assertEquals(score1+score1_increment, scoreTrack.getScore().get(player1));
        assertEquals(score2, scoreTrack.getScore().get(player2));
    }

    @Test
    void testGameEnded() {
        scoreTrack.updatePlayerScore(player1, 19);
        scoreTrack.updatePlayerScore(player2, 5);

        assertFalse(scoreTrack.gameEnded());

        scoreTrack.updatePlayerScore(player1, 1);

        assertTrue(scoreTrack.gameEnded());
    }
}
