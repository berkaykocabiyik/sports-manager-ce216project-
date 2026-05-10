package game;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int WEEKLY_TRAINING_SESSIONS = 3;

    private final SportType selectedSport;
    private final League league;
    private Team managedTeam;
    private int currentWeek = 1;
    private int trainingSessionsRemaining = WEEKLY_TRAINING_SESSIONS;
    private LocalDateTime lastSavedAt;
    private final List<String> recentResults = new ArrayList<>();

    public GameState(SportType selectedSport, League league) {
        this.selectedSport = selectedSport;
        this.league = league;
    }

    public List<Match> playCurrentWeek() {
        if (isSeasonFinished()) {
            return List.of();
        }

        List<Match> matches = league.playWeek(currentWeek);
        recentResults.add("Hafta " + currentWeek + " oynandı.");
        for (Match match : matches) {
            recentResults.add(match.getMatchResult().getSummary());
        }
        currentWeek++;
        trainingSessionsRemaining = isSeasonFinished() ? 0 : WEEKLY_TRAINING_SESSIONS;
        if (isSeasonFinished() && getChampion() != null) {
            recentResults.add("Sezon tamamlandı. Şampiyon: " + getChampion().getName());
        }
        return matches;
    }

    public String trainManagedPlayer(Player player) {
        if (managedTeam == null) {
            return "Önce yönetilecek takım seçilmeli.";
        }
        if (player == null) {
            return "Antrenman için oyuncu seçilmeli.";
        }
        if (!belongsToManagedTeam(player)) {
            return "Bu oyuncu yönetilen takıma ait değil.";
        }
        if (!player.canPlay()) {
            return "Sakat oyuncu antrenmana çıkamaz.";
        }
        if (trainingSessionsRemaining <= 0) {
            return "Bu hafta antrenman hakkı kalmadı.";
        }

        int gain = managedTeam.getCoach() != null && managedTeam.getCoach().getCoachingRating() >= 7.5
            ? 2
            : 1;
        int newRating = trainPlayer(player, gain);
        trainingSessionsRemaining--;
        String result = player.getName() + " antrenman yaptı. OVR +" + gain
            + " -> " + newRating
            + " | Kalan hak: " + trainingSessionsRemaining;
        recentResults.add(result);
        return result;
    }

    public String planManagedMatch(Formation formation, Tactic tactic) {
        if (managedTeam == null) {
            return "Önce yönetilecek takım seçilmeli.";
        }
        Match nextMatch = getNextManagedMatch();
        if (nextMatch == null) {
            return "Planlanacak maç bulunamadı.";
        }
        setManualPlan(managedTeam.getId(), formation, tactic);
        String result = "Maç planı hazırlandı: "
            + formation.getName() + " / " + tactic.getName()
            + " | Rakip: " + opponentName(nextMatch);
        recentResults.add(result);
        return result;
    }

    public boolean isSeasonFinished() {
        return league.getFixture() != null && currentWeek > league.getFixture().getTotalWeeks();
    }

    public Match getNextManagedMatch() {
        if (managedTeam == null || league.getFixture() == null) {
            return null;
        }
        return league.getFixture().getNextMatch(managedTeam);
    }

    public Team getChampion() {
        if (league.getStandings() == null || league.getStandings().getRankedTeams().length == 0) {
            return null;
        }
        return league.getStandings().getRankedTeams()[0];
    }

    public Formation getPlannedFormation() {
        if (managedTeam == null) return null;
        if (league instanceof VolleyballLeague volleyballLeague) {
            return volleyballLeague.getManualFormation(managedTeam.getId());
        }
        if (league instanceof FootballLeague footballLeague) {
            return footballLeague.getManualFormation(managedTeam.getId());
        }
        return null;
    }

    public Tactic getPlannedTactic() {
        if (managedTeam == null) return null;
        if (league instanceof VolleyballLeague volleyballLeague) {
            return volleyballLeague.getManualTactic(managedTeam.getId());
        }
        if (league instanceof FootballLeague footballLeague) {
            return footballLeague.getManualTactic(managedTeam.getId());
        }
        return null;
    }

    public SportType getSelectedSport() {
        return selectedSport;
    }

    public League getLeague() {
        return league;
    }

    public Team getManagedTeam() {
        return managedTeam;
    }

    public void setManagedTeam(Team managedTeam) {
        this.managedTeam = managedTeam;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = Math.max(1, currentWeek);
    }

    public int getTrainingSessionsRemaining() {
        return trainingSessionsRemaining;
    }

    public int getWeeklyTrainingSessions() {
        return WEEKLY_TRAINING_SESSIONS;
    }

    public LocalDateTime getLastSavedAt() {
        return lastSavedAt;
    }

    public void markSavedNow() {
        lastSavedAt = LocalDateTime.now();
    }

    public List<String> getRecentResults() {
        return new ArrayList<>(recentResults);
    }

    public void addRecentResult(String result) {
        recentResults.add(result);
    }

    private boolean belongsToManagedTeam(Player player) {
        for (Player squadPlayer : managedTeam.getSquad()) {
            if (squadPlayer != null && squadPlayer.getId().equals(player.getId())) {
                return true;
            }
        }
        return false;
    }

    private String opponentName(Match match) {
        if (match.getHomeTeam().getId().equals(managedTeam.getId())) {
            return match.getAwayTeam().getName();
        }
        return match.getHomeTeam().getName();
    }

    private int trainPlayer(Player player, int gain) {
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            volleyballPlayer.train(gain);
            return volleyballPlayer.getOverallRating();
        }
        if (player instanceof FootballPlayer footballPlayer) {
            footballPlayer.setOverallRating(footballPlayer.getOverallRating() + gain);
            return footballPlayer.getOverallRating();
        }
        player.setRating(player.getRating() + (gain / 10.0));
        return (int) Math.round(player.getRating() * 10);
    }

    private void setManualPlan(String teamId, Formation formation, Tactic tactic) {
        if (league instanceof VolleyballLeague volleyballLeague) {
            volleyballLeague.setManualPlan(teamId, formation, tactic);
        } else if (league instanceof FootballLeague footballLeague) {
            footballLeague.setManualPlan(teamId, formation, tactic);
        }
    }
}
