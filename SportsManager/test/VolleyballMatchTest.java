import game.Formation;
import game.MatchStatus;
import game.Position;
import game.Tactic;
import game.VolleyballCoach;
import game.VolleyballMatch;
import game.VolleyballPlayer;
import game.VolleyballSport;
import game.VolleyballTeam;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VolleyballMatchTest {

    @Test
    void simulatedMatchHasWinnerAndValidSetScore() {
        VolleyballTeam home = team("H", "Home");
        VolleyballTeam away = team("A", "Away");
        setup(home, away);
        setup(away, home);

        VolleyballMatch match = new VolleyballMatch("M1", home, away, LocalDateTime.now());
        match.simulateMatch();

        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertTrue(match.getHomeScore() == 3 || match.getAwayScore() == 3);
        assertTrue(match.getHomeScore() != match.getAwayScore());
        assertTrue(match.getSetScores().size() >= 3);
        assertTrue(match.getSetScores().size() <= 5);
    }

    private VolleyballTeam team(String id, String name) {
        VolleyballCoach coach = new VolleyballCoach("C" + id, "Coach " + name, 10, 7.0, "Balanced");
        VolleyballTeam team = new VolleyballTeam(id, name, "City", coach);
        for (int i = 0; i < VolleyballSport.SQUAD_SIZE; i++) {
            team.addPlayer(new VolleyballPlayer(id + "P" + i, name + " Player " + i,
                24, position(i), 65 + (i % 20)));
        }
        return team;
    }

    private Position position(int index) {
        if (index < 2) return Position.SETTER;
        if (index < 4) return Position.LIBERO;
        return Position.SPIKER;
    }

    private void setup(VolleyballTeam team, VolleyballTeam opponent) {
        Formation formation = team.getCoach().selectFormation(team);
        Tactic tactic = team.getCoach().selectTactic(team, opponent);
        team.setupLineup(formation, tactic);
    }
}
