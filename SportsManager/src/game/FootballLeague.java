package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FootballLeague extends League {

    private static final int MAX_TEAMS = 20;
    private int teamCount = 0;
    private final List<Match> playedMatches = new ArrayList<>();
    private Map<String, Formation> manualFormations = new HashMap<>();
    private Map<String, Tactic> manualTactics = new HashMap<>();

    public FootballLeague(String id, String name) {
        super(id, name, new FootballSport());
        this.teams = new Team[MAX_TEAMS];
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
                notifyObservers(team.getName() + " lige katıldı. (" + teamCount + "/" + MAX_TEAMS + ")");
                return;
            }
        }
    }

    @Override
    public void removeTeam(String teamId) {
        for (int i = 0; i < teams.length; i++) {
            if (teams[i] != null && teams[i].getId().equals(teamId)) {
                notifyObservers(teams[i].getName() + " ligden çıkartıldı.");
                teams[i] = null;
                teamCount--;
                return;
            }
        }
    }

    @Override
    public void generateFixture() {
        List<Team> activeTeams = getActiveTeams();
        fixture = new FootballFixture("Lig Fikstürü", activeTeams);
        notifyObservers("Çift devreli lig fikstürü oluşturuldu. Toplam maç: " +
            fixture.getAllMatches().length);
    }

    @Override
    public void updateStandings() {
        if (standings == null) {
            standings = new FootballStandings(teams);
        }
        notifyObservers("Puan tablosu güncellendi.");
    }

    @Override
    public void playMatch(Match match) {
        
        if (match.getHomeTeam() instanceof FootballTeam ht) {
            ht.decrementAllInjuries();
        }
        if (match.getAwayTeam() instanceof FootballTeam at) {
            at.decrementAllInjuries();
        }

        setupTeamForMatch(match.getHomeTeam(), match.getAwayTeam());
        setupTeamForMatch(match.getAwayTeam(), match.getHomeTeam());

        match.simulateMatch();
        clearManualPlan(match.getHomeTeam().getId());
        clearManualPlan(match.getAwayTeam().getId());
        playedMatches.add(match);

        updateStandings();
    }

    private void setupTeamForMatch(Team team, Team opponent) {
        if (team.getCoach() != null) {
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

    private void ensureManualPlans() {
        if (manualFormations == null) {
            manualFormations = new HashMap<>();
        }
        if (manualTactics == null) {
            manualTactics = new HashMap<>();
        }
    }

    public void playFullSeason() {
        if (fixture == null) {
            generateFixture();
        }

        int totalWeeks = fixture.getTotalWeeks();

        for (int week = 1; week <= totalWeeks; week++) {
            Match[] weekMatches = fixture.getWeekMatches(week);
            notifyObservers("\n========== Hafta " + week + " ==========");

            for (Match match : weekMatches) {
                if (match.getStatus() == MatchStatus.SCHEDULED) {
                    playMatch(match);
                }
            }
        }

        notifyObservers("\n========== SEZON SONU ==========");
        if (standings != null) {
            standings.printTable();
        }
    }

    public List<Team> getActiveTeams() {
        List<Team> active = new ArrayList<>();
        for (Team t : teams) {
            if (t != null) active.add(t);
        }
        return active;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public List<Match> getPlayedMatches() {
        return new ArrayList<>(playedMatches);
    }

    static class FootballFixture implements Fixture {

        private final String name;
        private final List<Match> matches = new ArrayList<>();
        private final List<List<Match>> weeklyMatches = new ArrayList<>();
        private final int totalWeeks;

        public FootballFixture(String name, List<Team> teams) {
            this.name = name;
            this.totalWeeks = generateDoubleRoundRobin(teams);
        }

        private int generateDoubleRoundRobin(List<Team> teams) {
            int n = teams.size();
            if (n < 2) return 0;

            List<Team> teamList = new ArrayList<>(teams);
            boolean hasBye = (n % 2 != 0);
            if (hasBye) {
                teamList.add(null); 
                n++;
            }

            int matchId = 0;
            java.time.LocalDateTime startDate = java.time.LocalDateTime.now();

            List<List<Match>> firstHalf = generateRoundRobin(teamList, n, matchId, startDate, false);
            for (List<Match> weekList : firstHalf) {
                weeklyMatches.add(weekList);
                matches.addAll(weekList);
                matchId += weekList.size();
            }

            java.time.LocalDateTime secondHalfStart = startDate.plusWeeks(firstHalf.size() + 1);
            List<List<Match>> secondHalf = generateRoundRobin(teamList, n, matchId, secondHalfStart, true);
            for (List<Match> weekList : secondHalf) {
                weeklyMatches.add(weekList);
                matches.addAll(weekList);
                matchId += weekList.size();
            }

            return weeklyMatches.size();
        }

        private List<List<Match>> generateRoundRobin(List<Team> teamList, int n, int startId,
                                                      java.time.LocalDateTime startDate, boolean reverse) {
            List<List<Match>> rounds = new ArrayList<>();
            int id = startId;

            List<Team> fixed = new ArrayList<>(teamList);

            for (int round = 0; round < n - 1; round++) {
                List<Match> weekMatches = new ArrayList<>();

                for (int i = 0; i < n / 2; i++) {
                    Team team1 = fixed.get(i);
                    Team team2 = fixed.get(n - 1 - i);

                    if (team1 != null && team2 != null) {
                        Team home = reverse ? team2 : team1;
                        Team away = reverse ? team1 : team2;

                        Match match = new FootballMatch(
                            "M" + (id++),
                            home,
                            away,
                            startDate.plusWeeks(round)
                        );
                        weekMatches.add(match);
                    }
                }

                rounds.add(weekMatches);

                Team last = fixed.remove(fixed.size() - 1);
                fixed.add(1, last);
            }

            return rounds;
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
                .filter(m -> m.getHomeTeam().getId().equals(team.getId()) ||
                             m.getAwayTeam().getId().equals(team.getId()))
                .toArray(Match[]::new);
        }

        @Override
        public Match getNextMatch(Team team) {
            return matches.stream()
                .filter(m -> (m.getHomeTeam().getId().equals(team.getId()) ||
                              m.getAwayTeam().getId().equals(team.getId()))
                    && m.getStatus() == MatchStatus.SCHEDULED)
                .findFirst()
                .orElse(null);
        }

        @Override
        public int getTotalWeeks() {
            return totalWeeks;
        }

        @Override
        public java.time.LocalDate getStartDate() {
            if (matches.isEmpty()) return java.time.LocalDate.now();
            return matches.get(0).getMatchDate().toLocalDate();
        }

        @Override
        public java.time.LocalDate getEndDate() {
            if (matches.isEmpty()) return java.time.LocalDate.now();
            return matches.get(matches.size() - 1).getMatchDate().toLocalDate();
        }
    }

    static class FootballStandings implements Standings {

        private final Team[] teams;
        private final Random random = new Random();

        public FootballStandings(Team[] teams) {
            this.teams = teams;
        }

        @Override
        public Team[] getRankedTeams() {
            List<Team> rankedList = new ArrayList<>();
            for (Team t : teams) {
                if (t != null) rankedList.add(t);
            }

            Collections.sort(rankedList, (t1, t2) -> {
                
                int points1 = getTeamPoints(t1);
                int points2 = getTeamPoints(t2);
                if (points1 != points2) return points2 - points1;

                double avg1 = t1.getGoalsAgainst() > 0
                    ? (double) t1.getGoalsFor() / t1.getGoalsAgainst() : Double.MAX_VALUE;
                double avg2 = t2.getGoalsAgainst() > 0
                    ? (double) t2.getGoalsFor() / t2.getGoalsAgainst() : Double.MAX_VALUE;
                if (Math.abs(avg1 - avg2) > 0.001) return Double.compare(avg2, avg1);

                int gd1 = t1.getGoalDifference();
                int gd2 = t2.getGoalDifference();
                if (gd1 != gd2) return gd2 - gd1;

                return random.nextBoolean() ? 1 : -1;
            });

            return rankedList.toArray(new Team[0]);
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
            return team.getTotalWins() * 3 + team.getTotalDraws();
        }

        @Override
        public int getTeamMatchesPlayed(Team team) {
            return team.getTotalWins() + team.getTotalDraws() + team.getTotalLosses();
        }

        @Override
        public int getTeamWins(Team team) {
            return team.getTotalWins();
        }

        @Override
        public int getTeamDraws(Team team) {
            return team.getTotalDraws();
        }

        @Override
        public int getTeamLosses(Team team) {
            return team.getTotalLosses();
        }

        @Override
        public String getTableDisplay() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n╔══════════════════════════════════════════════════════════════════════════════════╗\n");
            sb.append("║                           PUAN TABLOSU                                        ║\n");
            sb.append("╠════╦══════════════════════════╦════╦════╦════╦════╦════╦════╦══════╦═══════════╣\n");
            sb.append(String.format("║ %2s ║ %-24s ║ %2s ║ %2s ║ %2s ║ %2s ║ %2s ║ %2s ║ %4s ║ %9s ║\n",
                "Sı", "Takım", "O", "G", "B", "M", "AG", "YG", "Av", "Puan"));
            sb.append("╠════╬══════════════════════════╬════╬════╬════╬════╬════╬════╬══════╬═══════════╣\n");

            Team[] ranked = getRankedTeams();
            for (int i = 0; i < ranked.length; i++) {
                Team t = ranked[i];
                String avg = t.getGoalsAgainst() > 0
                    ? String.format("%.2f", (double) t.getGoalsFor() / t.getGoalsAgainst())
                    : "∞";
                sb.append(String.format("║ %2d ║ %-24s ║ %2d ║ %2d ║ %2d ║ %2d ║ %2d ║ %2d ║ %4s ║ %9d ║\n",
                    i + 1,
                    t.getName(),
                    getTeamMatchesPlayed(t),
                    getTeamWins(t),
                    getTeamDraws(t),
                    getTeamLosses(t),
                    t.getGoalsFor(),
                    t.getGoalsAgainst(),
                    avg,
                    getTeamPoints(t)
                ));
            }

            sb.append("╚════╩══════════════════════════╩════╩════╩════╩════╩════╩════╩══════╩═══════════╝\n");
            sb.append("Sıralama kriteri: Puan > Averaj > Gol Farkı > Kura\n");

            return sb.toString();
        }

        @Override
        public void printTable() {
            System.out.println(getTableDisplay());
        }
    }
}
