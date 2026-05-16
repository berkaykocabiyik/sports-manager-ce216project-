package game;

import java.util.ArrayList;
import java.util.List;

public class VolleyballTeam extends Team {

    private static final int MAX_SQUAD_SIZE = VolleyballSport.SQUAD_SIZE;
    private int totalRallyPointsFor;
    private int totalRallyPointsAgainst;

    public VolleyballTeam(String id, String name, String city, Coach coach) {
        super(id, name, city, coach);
        this.squad = new Player[MAX_SQUAD_SIZE];
        this.lineup = new Player[VolleyballSport.PLAYER_COUNT];
    }

    @Override
    public void addPlayer(Player player) {
        for (int i = 0; i < squad.length; i++) {
            if (squad[i] == null) {
                squad[i] = player;
                return;
            }
        }
        throw new RuntimeException("Voleybol kadrosu dolu! (Maks: " + MAX_SQUAD_SIZE + ")");
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
        double total = 0;
        int count = 0;
        for (Player player : squad) {
            if (player != null && player.canPlay()) {
                total += player.getRating();
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }

        double average = total / count;
        double coachBonus = coach == null ? 0 : coach.getSuccessRating() * 0.18;
        double formBonus = Math.max(-0.8, Math.min(0.8, (totalWins - totalLosses) * 0.08));
        return Math.min(10.0, average + coachBonus + formBonus);
    }

    @Override
    public void setupLineup(Formation formation, Tactic tactic) {
        this.formation = formation;
        this.currentTactic = tactic;
        if (coach != null) {
            coach.setupLineup(this, formation, tactic);
        }
    }

    @Override
    public TeamStats getStats() {
        return new VolleyballTeamStats(this);
    }

    public void recordVolleyballResult(int setsFor, int setsAgainst,
                                       int rallyPointsFor, int rallyPointsAgainst,
                                       boolean isWin) {
        recordMatchResult(setsFor, setsAgainst, isWin, false);
        this.totalRallyPointsFor += rallyPointsFor;
        this.totalRallyPointsAgainst += rallyPointsAgainst;
    }

    public int getSquadSize() {
        int count = 0;
        for (Player player : squad) {
            if (player != null) count++;
        }
        return count;
    }

    public int getAvailablePlayerCount() {
        int count = 0;
        for (Player player : squad) {
            if (player != null && player.canPlay()) count++;
        }
        return count;
    }

    public List<Player> getSubstitutes() {
        List<Player> substitutes = new ArrayList<>();
        for (Player player : squad) {
            if (player != null && player.canPlay() && !isInLineup(player)) {
                substitutes.add(player);
            }
        }
        return substitutes;
    }

    public void decrementAllInjuries() {
        for (Player player : squad) {
            if (player != null && player.isInjured()) {
                player.decrementInjury();
            }
        }
    }

    public int getSetsFor() {
        return goalsFor;
    }

    public int getSetsAgainst() {
        return goalsAgainst;
    }

    public int getSetDifference() {
        return goalsFor - goalsAgainst;
    }

    public double getSetAverage() {
        return goalsAgainst == 0 ? goalsFor : (double) goalsFor / goalsAgainst;
    }

    public int getTotalRallyPointsFor() {
        return totalRallyPointsFor;
    }

    public int getTotalRallyPointsAgainst() {
        return totalRallyPointsAgainst;
    }

    public int getRallyPointDifference() {
        return totalRallyPointsFor - totalRallyPointsAgainst;
    }

    private boolean isInLineup(Player player) {
        for (Player lineupPlayer : lineup) {
            if (lineupPlayer != null && lineupPlayer.getId().equals(player.getId())) {
                return true;
            }
        }
        return false;
    }

    private static class VolleyballTeamStats implements TeamStats {

        private final VolleyballTeam team;

        VolleyballTeamStats(VolleyballTeam team) {
            this.team = team;
        }

        @Override
        public String getTeamName() {
            return team.getName();
        }

        @Override
        public int getMatchesPlayed() {
            return team.getTotalWins() + team.getTotalLosses();
        }

        @Override
        public int getPoints() {
            return team.getTotalWins() * 3;
        }

        @Override
        public int getGoalsFor() {
            return team.getSetsFor();
        }

        @Override
        public int getGoalsAgainst() {
            return team.getSetsAgainst();
        }

        @Override
        public int getGoalDifference() {
            return team.getSetDifference();
        }

        @Override
        public double getFormRating() {
            int played = getMatchesPlayed();
            return played == 0 ? 0 : (double) team.getTotalWins() / played;
        }

        @Override
        public String getSummary() {
            return String.format(
                "%s - O: %d | G: %d | M: %d | P: %d | Set: %d-%d | Sayı: %d-%d",
                team.getName(),
                getMatchesPlayed(),
                team.getTotalWins(),
                team.getTotalLosses(),
                getPoints(),
                team.getSetsFor(),
                team.getSetsAgainst(),
                team.getTotalRallyPointsFor(),
                team.getTotalRallyPointsAgainst()
            );
        }
    }
}
