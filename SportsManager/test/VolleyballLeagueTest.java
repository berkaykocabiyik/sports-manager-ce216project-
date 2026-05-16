import game.GameFactory;
import game.Match;
import game.MatchStatus;
import game.Player;
import game.VolleyballLeague;
import game.VolleyballTeam;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VolleyballLeagueTest {

    @Test
    void createsTwentyTeamDoubleRoundRobinFixture() {
        VolleyballLeague league = GameFactory.createVolleyballLeague();

        assertEquals(14, league.getTeamCount());
        assertEquals(26, league.getFixture().getTotalWeeks());
        assertEquals(182, league.getFixture().getAllMatches().length);
    }

    @Test
    void playsWeekWithoutDraws() {
        VolleyballLeague league = GameFactory.createVolleyballLeague();

        List<Match> matches = league.playWeek(1);

        assertEquals(7, matches.size());
        for (Match match : matches) {
            assertEquals(MatchStatus.FINISHED, match.getStatus());
            assertTrue(match.getHomeScore() == 3 || match.getAwayScore() == 3);
            assertTrue(match.getHomeScore() != match.getAwayScore());
        }
    }

    @Test
    void injuredPlayerMissesNextMatchThenRecovers() {
        VolleyballLeague league = GameFactory.createVolleyballLeague();
        VolleyballTeam team = (VolleyballTeam) league.getActiveTeams().get(0);
        Player injured = team.getSquad()[0];
        injured.setInjured(1);

        league.playWeek(1);

        for (Player lineupPlayer : team.getLineup()) {
            if (lineupPlayer != null) {
                assertTrue(!lineupPlayer.getId().equals(injured.getId()));
            }
        }
        assertEquals(0, injured.getInjuredMatchesRemaining());
    }
}
