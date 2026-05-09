import game.GameFactory;
import game.GameState;
import game.Match;
import game.MatchStatus;
import game.SportType;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FootballFirstSportTest {

    @Test
    void createsAndPlaysFootballAsFirstSport() {
        GameState state = GameFactory.createGameState(SportType.FOOTBALL);
        state.setManagedTeam(state.getLeague().getActiveTeams().get(0));

        assertEquals("Futbol", state.getLeague().getSport().getName());
        assertEquals(18, state.getLeague().getTeamCount());

        List<Match> matches = state.playCurrentWeek();

        assertFalse(matches.isEmpty());
        assertEquals(MatchStatus.FINISHED, matches.get(0).getStatus());
    }
}
