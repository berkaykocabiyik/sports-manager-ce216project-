import java.util.List;

public class LeagueFactory {

    public static League createLeague(List<String> teams) {

        League league = new League(teams);
        league.generateFixtures();

        return league;
    }
}