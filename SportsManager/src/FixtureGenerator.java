import java.util.*;

public class FixtureGenerator {

    public static List<Fixture> generateRoundRobin(List<String> teams) {

        List<String> teamList = new ArrayList<>(teams);

        if (teamList.size() % 2 != 0) {
            teamList.add("BYE");
        }

        int numTeams = teamList.size();
        int numRounds = numTeams - 1;
        int matchesPerRound = numTeams / 2;

        List<Fixture> fixtures = new ArrayList<>();

        for (int round = 0; round < numRounds; round++) {

            List<Match> matches = new ArrayList<>();

            for (int i = 0; i < matchesPerRound; i++) {

                String home = teamList.get(i);
                String away = teamList.get(numTeams - 1 - i);

                if (!home.equals("BYE") && !away.equals("BYE")) {
                    matches.add(new Match(home, away));
                }
            }

            fixtures.add(new Fixture(round + 1, matches));

            Collections.rotate(teamList.subList(1, teamList.size()), 1);
        }

        return fixtures;
    }
}