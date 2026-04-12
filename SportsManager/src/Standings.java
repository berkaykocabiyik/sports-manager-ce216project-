import java.util.*;

public class Standings {

    private Map<String, StandingsEntry> table = new HashMap<>();

    public Standings(List<String> teams) {
        for (String t : teams) {
            table.put(t, new StandingsEntry(t));
        }
    }

    public void update(String home, String away, int homeScore, int awayScore) {

        StandingsEntry h = table.get(home);
        StandingsEntry a = table.get(away);

        h.played++;
        a.played++;

        h.goalsFor += homeScore;
        h.goalsAgainst += awayScore;

        a.goalsFor += awayScore;
        a.goalsAgainst += homeScore;

        if (homeScore > awayScore) {
            h.wins++;
            a.losses++;
            h.points += 3;
        } else if (homeScore < awayScore) {
            a.wins++;
            h.losses++;
            a.points += 3;
        } else {
            h.draws++;
            a.draws++;
            h.points += 1;
            a.points += 1;
        }
    }

    public List<StandingsEntry> getTable() {

        List<StandingsEntry> list = new ArrayList<>(table.values());

        list.sort((a, b) -> {

            if (b.points != a.points)
                return b.points - a.points;

            int gdA = a.goalDifference();
            int gdB = b.goalDifference();

            if (gdB != gdA)
                return gdB - gdA;

            return b.goalsFor - a.goalsFor;
        });

        return list;
    }
}