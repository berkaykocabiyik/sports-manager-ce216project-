public class StandingsEntry {

    String team;
    int played;
    int wins;
    int draws;
    int losses;
    int goalsFor;
    int goalsAgainst;
    int points;

    public StandingsEntry(String team) {
        this.team = team;
    }

    public int goalDifference() {
        return goalsFor - goalsAgainst;
    }
}