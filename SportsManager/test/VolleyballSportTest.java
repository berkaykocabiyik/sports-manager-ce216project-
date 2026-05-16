import game.Position;
import game.VolleyballSport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VolleyballSportTest {

    @Test
    void exposesVolleyballRules() {
        VolleyballSport sport = new VolleyballSport();

        assertEquals("Voleybol", sport.getName());
        assertEquals(6, sport.getPlayerCount());
        assertEquals(6, sport.getSubstituteCount());
        assertEquals(5, sport.getMatchPeriods());
        assertEquals(3, sport.getWinPoints());
        assertEquals(0, sport.getDrawPoints());
        assertEquals(Position.SETTER, sport.getValidPositions()[0]);
    }

    @Test
    void validatesBestOfFiveResults() {
        VolleyballSport sport = new VolleyballSport();

        assertTrue(sport.isValidMatchResult(3, 0));
        assertTrue(sport.isValidMatchResult(2, 3));
        assertFalse(sport.isValidMatchResult(2, 2));
        assertFalse(sport.isValidMatchResult(4, 1));
    }
}
