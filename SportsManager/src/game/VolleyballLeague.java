package game;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyballLeague extends League {

    private static final int MAX_TEAMS = VolleyballSport.TEAM_COUNT;
    private int teamCount;
    private final List<Match> playedMatches = new ArrayList<>();
    private Map<String, Formation> manualFormations = new HashMap<>();
    private Map<String, Tactic> manualTactics = new HashMap<>();

    public VolleyballLeague(String id, String name) {
        super(id, name, new VolleyballSport());
        this.teams = new Team[MAX_TEAMS];
        this.standings = new VolleyballStandings(this.teams);
    }

    @Override
    public void addTeam(Team team) {
        if (teamCount >= MAX_TEAMS) {
            throw new RuntimeException("Lig kapasitesi dolu! (Maks: " + MAX_TEAMS + " takım)");
        }
        for (int i = 0; i < teams.length; i++) {
            if (teams[i] == null) {
                teams[i] = team;
                teamCount++;
                notifyObservers(team.getName() + " lige katıldı.");
                return;
            }
        }
    }

    @Override
    public void removeTeam(String teamId) {
        for (int i = 0; i < teams.length; i++) {
            if (teams[i] != null && teams[i].getId().equals(teamId)) {
                teams[i] = null;
                teamCount--;
                notifyObservers("Takım ligden çıkarıldı: " + teamId);
                return;
            }
        }
    }

    @Override
    public void generateFixture() {
        fixture = new VolleyballFixture("Voleybol Ligi Fikstürü", getActiveTeams());
        notifyObservers("Voleybol fikstürü oluşturuldu. Maç sayısı: "
            + fixture.getAllMatches().length);
    }

    @Override
    public void updateStandings() {
        if (standings == null) {
            standings = new VolleyballStandings(teams);
        }
        notifyObservers("Puan tablosu güncellendi.");
    }

    @Override
    public void playMatch(Match match) {
        if (match == null || match.getStatus() != MatchStatus.SCHEDULED) {
            return;
        }

        List<String> homeInjuredBeforeMatch = injuredPlayerIds(match.getHomeTeam());
        List<String> awayInjuredBeforeMatch = injuredPlayerIds(match.getAwayTeam());

        setupTeamForMatch(match.getHomeTeam(), match.getAwayTeam());
        setupTeamForMatch(match.getAwayTeam(), match.getHomeTeam());

        match.simulateMatch();
        clearManualPlan(match.getHomeTeam().getId());
        clearManualPlan(match.getAwayTeam().getId());
        decrementTrackedInjuries(match.getHomeTeam(), homeInjuredBeforeMatch);
        decrementTrackedInjuries(match.getAwayTeam(), awayInjuredBeforeMatch);
        playedMatches.add(match);
        updateStandings();
    }

    public List<Match> playWeek(int week) {
        if (fixture == null) {
            generateFixture();
        }
        List<Match> results = new ArrayList<>();
        for (Match match : fixture.getWeekMatches(week)) {
            if (match.getStatus() == MatchStatus.SCHEDULED) {
                playMatch(match);
                results.add(match);
            }
        }
        return results;
    }

    public List<Team> getActiveTeams() {
        List<Team> activeTeams = new ArrayList<>();
        for (Team team : teams) {
            if (team != null) {
                activeTeams.add(team);
            }
        }
        return activeTeams;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public List<Match> getPlayedMatches() {
        return new ArrayList<>(playedMatches);
    }

    public void setManualPlan(String teamId, Formation formation, Tactic tactic) {
        ensureManualPlans();
        if (formation != null) {
            manualFormations.put(teamId, formation);
        }
        if (tactic != null) {
            manualTactics.put(teamId, tactic);
        }
    }

    public void clearManualPlan(String teamId) {
        ensureManualPlans();
        manualFormations.remove(teamId);
        manualTactics.remove(teamId);
    }

    public Formation getManualFormation(String teamId) {
        ensureManualPlans();
        return manualFormations.get(teamId);
    }

    public Tactic getManualTactic(String teamId) {
        ensureManualPlans();
        return manualTactics.get(teamId);
    }

    private void setupTeamForMatch(Team team, Team opponent) {
        if (team.getCoach() == null) {
            return;
        }
        ensureManualPlans();
        Formation formation = manualFormations.getOrDefault(
            team.getId(),
            team.getCoach().selectFormation(team)
        );
        Tactic tactic = manualTactics.getOrDefault(
            team.getId(),
            team.getCoach().selectTactic(team, opponent)
        );
        team.setupLineup(formation, tactic);
    }

    private void ensureManualPlans() {
        if (manualFormations == null) {
            manualFormations = new HashMap<>();
        }
        if (manualTactics == null) {
            manualTactics = new HashMap<>();
        }
    }

    private List<String> injuredPlayerIds(Team team) {
        List<String> playerIds = new ArrayList<>();
        for (Player player : team.getSquad()) {
            if (player != null && player.isInjured()) {
                playerIds.add(player.getId());
            }
        }
        return playerIds;
    }

    private void decrementTrackedInjuries(Team team, List<String> playerIds) {
        for (Player player : team.getSquad()) {
            if (player != null && playerIds.contains(player.getId())) {
                player.decrementInjury();
            }
        }
    }

    static class VolleyballFixture implements Fixture {

        private final String name;
        private final List<Match> matches = new ArrayList<>();
        private final List<List<Match>> weeklyMatches = new ArrayList<>();
        private int totalWeeks;

        VolleyballFixture(String name, List<Team> teams) {
            this.name = name;
            this.totalWeeks = generateDoubleRoundRobin(teams);
        }

        private int generateDoubleRoundRobin(List<Team> teams) {
            int teamSize = teams.size();
            if (teamSize < 2) {
                return 0;
            }

            List<Team> rotation = new ArrayList<>(teams);
            if (teamSize % 2 != 0) {
                rotation.add(null);
                teamSize++;
            }

            int matchId = 1;
            LocalDateTime start = LocalDateTime.now().plusDays(7);
            matchId = addHalf(rotation, teamSize, matchId, start, false);
            addHalf(rotation, teamSize, matchId, start.plusWeeks(teamSize), true);
            return weeklyMatches.size();
        }

        private int addHalf(List<Team> teams, int teamSize, int matchId,
                            LocalDateTime start, boolean reverseHomeAway) {
            List<Team> rotation = new ArrayList<>(teams);
            for (int round = 0; round < teamSize - 1; round++) {
                List<Match> week = new ArrayList<>();
                for (int i = 0; i < teamSize / 2; i++) {
                    Team first = rotation.get(i);
                    Team second = rotation.get(teamSize - 1 - i);
                    if (first != null && second != null) {
                        Team home = reverseHomeAway ? second : first;
                        Team away = reverseHomeAway ? first : second;
                        Match match = new VolleyballMatch(
                            "V" + matchId++,
                            home,
                            away,
                            start.plusWeeks(round)
                        );
                        week.add(match);
                    }
                }
                weeklyMatches.add(week);
                matches.addAll(week);

                Team last = rotation.remove(rotation.size() - 1);
                rotation.add(1, last);
            }
            return matchId;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Match[] getAllMatches() {
            return matches.toArray(new Match[0]);
        }

        @Override
        public Match[] getWeekMatches(int week) {
            if (week < 1 || week > weeklyMatches.size()) {
                return new Match[0];
            }
            return weeklyMatches.get(week - 1).toArray(new Match[0]);
        }

        @Override
        public Match[] getTeamMatches(Team team) {
            return matches.stream()
                .filter(match -> match.getHomeTeam().getId().equals(team.getId())
                    || match.getAwayTeam().getId().equals(team.getId()))
                .toArray(Match[]::new);
        }

        @Override
        public Match getNextMatch(Team team) {
            return matches.stream()
                .filter(match -> match.getStatus() == MatchStatus.SCHEDULED)
                .filter(match -> match.getHomeTeam().getId().equals(team.getId())
                    || match.getAwayTeam().getId().equals(team.getId()))
                .findFirst()
                .orElse(null);
        }

        @Override
        public int getTotalWeeks() {
            return totalWeeks;
        }

        @Override
        public LocalDate getStartDate() {
            if (matches.isEmpty()) {
                return LocalDate.now();
            }
            return matches.get(0).getMatchDate().toLocalDate();
        }

        @Override
        public LocalDate getEndDate() {
            if (matches.isEmpty()) {
                return LocalDate.now();
            }
            return matches.get(matches.size() - 1).getMatchDate().toLocalDate();
        }
    }

    static class VolleyballStandings implements Standings {

        private final Team[] teams;

        VolleyballStandings(Team[] teams) {
            this.teams = teams;
        }

        @Override
        public Team[] getRankedTeams() {
            List<Team> ranked = new ArrayList<>();
            for (Team team : teams) {
                if (team != null) {
                    ranked.add(team);
                }
            }

            ranked.sort((first, second) -> {
                int byPoints = Integer.compare(getTeamPoints(second), getTeamPoints(first));
                if (byPoints != 0) return byPoints;

                int byAverage = Double.compare(setAverage(second), setAverage(first));
                if (byAverage != 0) return byAverage;

                int bySetDifference = Integer.compare(second.getGoalDifference(), first.getGoalDifference());
                if (bySetDifference != 0) return bySetDifference;

                int byRallyDifference = Integer.compare(
                    rallyPointDifference(second),
                    rallyPointDifference(first)
                );
                if (byRallyDifference != 0) return byRallyDifference;

                return first.getName().compareTo(second.getName());
            });
            return ranked.toArray(new Team[0]);
        }

        @Override
        public int getTeamRank(Team team) {
            Team[] ranked = getRankedTeams();
            for (int i = 0; i < ranked.length; i++) {
                if (ranked[i].getId().equals(team.getId())) {
                    return i + 1;
                }
            }
            return -1;
        }

        @Override
        public int getTeamPoints(Team team) {
            return team.getTotalWins() * 3;
        }

        @Override
        public int getTeamMatchesPlayed(Team team) {
            return team.getTotalWins() + team.getTotalLosses();
        }

        @Override
        public int getTeamWins(Team team) {
            return team.getTotalWins();
        }

        @Override
        public int getTeamDraws(Team team) {
            return 0;
        }

        @Override
        public int getTeamLosses(Team team) {
            return team.getTotalLosses();
        }

        @Override
        public String getTableDisplay() {
            StringBuilder builder = new StringBuilder();
            builder.append("\nVOLEYBOL PUAN TABLOSU\n");
            builder.append(String.format("%-3s %-24s %3s %3s %3s %5s %8s %8s %6s%n",
                "S", "Takım", "O", "G", "M", "P", "Set", "Sayı", "Ort"));

            Team[] ranked = getRankedTeams();
            for (int i = 0; i < ranked.length; i++) {
                Team team = ranked[i];
                String rally = "-";
                if (team instanceof VolleyballTeam volleyballTeam) {
                    rally = volleyballTeam.getTotalRallyPointsFor()
                        + "-" + volleyballTeam.getTotalRallyPointsAgainst();
                }
                builder.append(String.format("%-3d %-24s %3d %3d %3d %5d %8s %8s %6.2f%n",
                    i + 1,
                    team.getName(),
                    getTeamMatchesPlayed(team),
                    getTeamWins(team),
                    getTeamLosses(team),
                    getTeamPoints(team),
                    team.getGoalsFor() + "-" + team.getGoalsAgainst(),
                    rally,
                    setAverage(team)
                ));
            }
            builder.append("Sıralama: Puan > Set averajı > Set farkı > Sayı farkı > Takım adı\n");
            return builder.toString();
        }

        @Override
        public void printTable() {
            System.out.println(getTableDisplay());
        }

        private double setAverage(Team team) {
            if (team.getGoalsAgainst() == 0) {
                return team.getGoalsFor();
            }
            return (double) team.getGoalsFor() / team.getGoalsAgainst();
        }

        private int rallyPointDifference(Team team) {
            if (team instanceof VolleyballTeam volleyballTeam) {
                return volleyballTeam.getRallyPointDifference();
            }
            return 0;
        }
    }
}
