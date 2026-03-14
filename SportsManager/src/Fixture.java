import java.util.List;

public class Fixture {

    private int week;
    private List<Match> matches;

    public Fixture(int week, List<Match> matches) {
        this.week = week;
        this.matches = matches;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public int getWeek() {
        return week;
    }
}