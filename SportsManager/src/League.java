import java.util.List;

public class League implements ILeague {

    private List<String> teams;
    private List<Fixture> fixtures;
    private Standings standings;

    public League(List<String> teams) {
        this.teams = teams;
        this.standings = new Standings(teams);
    }

    public void generateFixtures() {
        this.fixtures = FixtureGenerator.generateRoundRobin(teams);
    }

    public void recordMatchResult(String home, String away, int homeScore, int awayScore) {
        standings.update(home, away, homeScore, awayScore);
    }

    public Standings getStandings() {
        return standings;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }
}