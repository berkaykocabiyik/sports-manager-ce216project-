package game;

import java.util.ArrayList;
import java.util.List;

public class FootballTeam extends Team {

    private static final int MAX_SQUAD_SIZE = 18; 
    private int substitutionsUsed;
    private static final int MAX_SUBSTITUTIONS = 3; 

    public FootballTeam(String id, String name, String city, Coach coach) {
        super(id, name, city, coach);
        this.squad = new Player[MAX_SQUAD_SIZE];
        this.lineup = new Player[11];
        this.substitutionsUsed = 0;
    }

    @Override
    public void addPlayer(Player player) {
        for (int i = 0; i < squad.length; i++) {
            if (squad[i] == null) {
                squad[i] = player;
                return;
            }
        }
        throw new RuntimeException("Takım kadro kapasitesi dolu! (Maks: " + MAX_SQUAD_SIZE + ")");
    }

    @Override
    public void removePlayer(String playerId) {
        for (int i = 0; i < squad.length; i++) {
            if (squad[i] != null && squad[i].getId().equals(playerId)) {
                squad[i] = null;
                return;
            }
        }
    }

    @Override
    public double calculateTeamStrength() {
        double totalStrength = 0;
        int playerCount = 0;

        for (Player player : squad) {
            if (player != null && player.canPlay()) {
                totalStrength += player.getRating();
                playerCount++;
            }
        }

        if (playerCount == 0) return 0;

        double averageRating = totalStrength / playerCount;
        double coachBonus = coach.getSuccessRating() * 0.2;
        double formBonus = (totalWins - totalLosses) * 0.1;

        return Math.min(10.0, averageRating + coachBonus + formBonus);
    }

    @Override
    public void setupLineup(Formation formation, Tactic tactic) {
        this.formation = formation;
        this.currentTactic = tactic;
        this.substitutionsUsed = 0;
        coach.setupLineup(this, formation, tactic);
    }

    @Override
    public TeamStats getStats() {
        return new FootballTeamStats(this);
    }

    public int getSquadSize() {
        int count = 0;
        for (Player p : squad) {
            if (p != null) count++;
        }
        return count;
    }

    public int getAvailablePlayerCount() {
        int count = 0;
        for (Player p : squad) {
            if (p != null && p.canPlay()) count++;
        }
        return count;
    }

    public List<Player> getSubstitutes() {
        List<Player> subs = new ArrayList<>();
        for (Player p : squad) {
            if (p != null && p.canPlay() && !isInLineup(p)) {
                subs.add(p);
            }
        }
        return subs;
    }

    public boolean makeSubstitution(Player out, Player in) {
        if (substitutionsUsed >= MAX_SUBSTITUTIONS) {
            return false;
        }

        for (int i = 0; i < lineup.length; i++) {
            if (lineup[i] != null && lineup[i].getId().equals(out.getId())) {
                lineup[i] = in;
                substitutionsUsed++;
                return true;
            }
        }
        return false;
    }

    public void changeTactic(Tactic newTactic) {
        this.currentTactic = newTactic;
    }

    public void changeFormation(Formation newFormation) {
        this.formation = newFormation;
        coach.setupLineup(this, newFormation, currentTactic);
    }

    public void resetSubstitutions() {
        this.substitutionsUsed = 0;
    }

    public int getSubstitutionsUsed() {
        return substitutionsUsed;
    }

    public void decrementAllInjuries() {
        for (Player p : squad) {
            if (p != null && p.isInjured()) {
                p.decrementInjury();
            }
        }
    }

    private boolean isInLineup(Player player) {
        for (Player p : lineup) {
            if (p != null && p.getId().equals(player.getId())) {
                return true;
            }
        }
        return false;
    }

    private static class FootballTeamStats implements TeamStats {

        private final FootballTeam team;

        public FootballTeamStats(FootballTeam team) {
            this.team = team;
        }

        @Override
        public String getTeamName() {
            return team.getName();
        }

        @Override
        public int getMatchesPlayed() {
            return team.getTotalWins() + team.getTotalDraws() + team.getTotalLosses();
        }

        @Override
        public int getPoints() {
            return team.getTotalWins() * 3 + team.getTotalDraws();
        }

        @Override
        public int getGoalsFor() {
            return team.getGoalsFor();
        }

        @Override
        public int getGoalsAgainst() {
            return team.getGoalsAgainst();
        }

        @Override
        public int getGoalDifference() {
            return team.getGoalDifference();
        }

        @Override
        public double getFormRating() {
            int matchesPlayed = getMatchesPlayed();
            if (matchesPlayed == 0) return 0;
            int recentMatches = Math.min(5, matchesPlayed);
            return (team.getTotalWins() * 3.0 + team.getTotalDraws()) / recentMatches;
        }

        @Override
        public String getSummary() {
            return String.format(
                "%s - Oy: %d | G: %d | B: %d | M: %d | P: %d | AG: %d | YG: %d | Av: %s",
                team.getName(),
                getMatchesPlayed(),
                team.getTotalWins(),
                team.getTotalDraws(),
                team.getTotalLosses(),
                getPoints(),
                getGoalsFor(),
                getGoalsAgainst(),
                team.getGoalsAgainst() > 0
                    ? String.format("%.2f", (double) team.getGoalsFor() / team.getGoalsAgainst())
                    : "∞"
            );
        }
    }
}
