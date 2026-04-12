package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MatchSimulationEngine extends Match {

    protected List<String> matchEvents = new ArrayList<>();
    protected List<Player> goalScorers = new ArrayList<>();
    protected List<String> periodSummaries = new ArrayList<>();
    protected Random random = new Random();

    protected double goalChancePerMinute = 0.03;
    protected double injuryChancePerMinute = 0.005;

    protected double homeAdvantageBonus = 0.5;
    protected double tacticAggressivenessWeight = 0.1;

    public MatchSimulationEngine(String id, Team homeTeam, Team awayTeam, Sport sport,
                                 java.time.LocalDateTime matchDate) {
        super(id, homeTeam, awayTeam, sport, matchDate);
        this.sport = sport;
    }

    @Override
    public final void simulateMatch() {
        notifyObservers("⏯️ Maç simülasyonu başlıyor...");

        prepareMatch();

        int periods = sport.getMatchPeriods();
        for (int period = 1; period <= periods; period++) {
            currentPeriod = period;

            beforePeriod(period);

            simulatePeriod(period);

            afterPeriod(period);

            if (period < periods) {
                periodSummaries.add(generatePeriodSummary(period));
                notifyObservers("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        }

        recordResults();

        afterMatch();

        finishMatch();
        notifyObservers(String.format("✅ Maç Bitti: %s %d-%d %s",
                homeTeam.getName(), homeScore, awayScore, awayTeam.getName()));
    }

    protected void simulatePeriod(int period) {
        notifyObservers(String.format("=== %s %d. Bölüm Başladı ===",
                sport.getName(), period));

        int periodDuration = sport.getPeriodDuration();
        int startMinute = (period - 1) * periodDuration + 1;
        int endMinute = period * periodDuration;

        for (int minute = startMinute; minute <= endMinute; minute++) {
            currentMinute = minute;

            if (random.nextDouble() < calculateGoalChance()) {
                Team scoringTeam = selectScoringTeam();
                if (scoringTeam != null) {
                    Player scorer = selectScorer(scoringTeam);
                    if (scorer != null && scorer.canPlay()) {
                        scoreGoal(scoringTeam, scorer, minute);

                        handleAssist(scoringTeam, scorer, minute);
                    }
                }
            }

            simulateOtherEvents(minute, period);

            if (random.nextDouble() < injuryChancePerMinute) {
                Team injuredTeam = random.nextBoolean() ? homeTeam : awayTeam;
                Player injuredPlayer = getRandomFieldPlayer(injuredTeam);
                if (injuredPlayer != null) {
                    notifyObservers(String.format("🏥 %d' - %s (%s) sakatlandı!",
                            minute, injuredPlayer.getName(), injuredTeam.getName()));
                }
            }
        }

        notifyObservers(String.format("--- %s Sonrası Skor: %s %d-%d %s ---",
                period < sport.getMatchPeriods() ? "Devre" : "Maç",
                homeTeam.getName(), homeScore, awayScore, awayTeam.getName()));
    }

    protected double calculateGoalChance() {
        double homeStrength = homeTeam.calculateTeamStrength();
        double awayStrength = awayTeam.calculateTeamStrength();

        if (homeTeam.getCurrentTactic() != null) {
            homeStrength += homeTeam.getCurrentTactic().getAggressivenessLevel()
                    * tacticAggressivenessWeight;
        }
        if (awayTeam.getCurrentTactic() != null) {
            awayStrength += awayTeam.getCurrentTactic().getAggressivenessLevel()
                    * tacticAggressivenessWeight;
        }

        homeStrength += homeAdvantageBonus;

        double totalStrength = homeStrength + awayStrength;
        double baseChance = goalChancePerMinute;

        if (totalStrength > 0) {
            double strengthFactor = (homeStrength / totalStrength);
            return baseChance * (strengthFactor + 1.0);
        }

        return baseChance;
    }

    protected Team selectScoringTeam() {
        double homeChance = calculateTeamGoalChance(homeTeam);
        double awayChance = calculateTeamGoalChance(awayTeam);
        double total = homeChance + awayChance;

        if (total <= 0) return null;

        double rand = random.nextDouble() * total;
        return (rand < homeChance) ? homeTeam : awayTeam;
    }

    protected double calculateTeamGoalChance(Team team) {
        double baseStrength = team.calculateTeamStrength();

        if (team.getCurrentTactic() != null) {
            baseStrength += team.getCurrentTactic().getAggressivenessLevel()
                    * tacticAggressivenessWeight;
        }

        int teamScore = (team == homeTeam) ? homeScore : awayScore;
        int oppScore = (team == homeTeam) ? awayScore : homeScore;

        if (teamScore < oppScore) {
            baseStrength *= 1.2;
        } else if (teamScore > oppScore) {
            baseStrength *= 0.8;
        }

        return baseStrength;
    }

    protected Player selectScorer(Team team) {
        if (!(team instanceof FootballTeam ft)) {
            return getRandomFieldPlayer(team);
        }

        Player[] lineup = ft.getLineup();
        List<Player> strikers = new ArrayList<>();

        for (Player p : lineup) {
            if (p != null && p.getPosition() == Position.FORWARD && p.canPlay()) {
                strikers.add(p);
            }
        }

        if (strikers.isEmpty()) {

            for (Player p : lineup) {
                if (p != null && p.getPosition() == Position.MIDFIELDER && p.canPlay()) {
                    strikers.add(p);
                }
            }
        }

        if (strikers.isEmpty()) {
            return getRandomFieldPlayer(team);
        }

        Player best = strikers.get(0);
        for (Player p : strikers) {
            if (p instanceof FootballPlayer fp1 && best instanceof FootballPlayer fp2) {
                if (fp1.getOverallRating() > fp2.getOverallRating()) {
                    best = p;
                }
            }
        }

        return best;
    }

    protected void handleAssist(Team team, Player scorer, int minute) {
        if (random.nextDouble() < 0.5) {
            Player assister = selectAssister(team, scorer);
            if (assister != null && assister instanceof FootballPlayer fp) {
                fp.addAssist();
                notifyObservers(String.format("  🅰️ Asist: %s", assister.getName()));
            }
        }
    }

    protected Player selectAssister(Team team, Player scorer) {
        Player[] lineup = (team instanceof FootballTeam ft) ? ft.getLineup() : null;
        if (lineup == null) {
            return getRandomFieldPlayer(team);
        }

        List<Player> midfielders = new ArrayList<>();
        for (Player p : lineup) {
            if (p != null && p != scorer && p.canPlay() &&
                    (p.getPosition() == Position.MIDFIELDER ||
                            p.getPosition() == Position.FORWARD)) {
                midfielders.add(p);
            }
        }

        return midfielders.isEmpty() ?
                getRandomFieldPlayer(team) :
                midfielders.get(random.nextInt(midfielders.size()));
    }

    protected void scoreGoal(Team team, Player player, int minute) {
        if (team.getId().equals(homeTeam.getId())) {
            homeScore++;
        } else {
            awayScore++;
        }

        if (player instanceof FootballPlayer fp) {
            fp.addGoal();
        }

        goalScorers.add(player);
        String event = String.format("⚽ %d' - GOL! %s (%s) | Skor: %s %d-%d %s",
                minute, player.getName(), team.getName(),
                homeTeam.getName(), homeScore, awayScore, awayTeam.getName());
        matchEvents.add(event);
        notifyObservers(event);
    }

    protected boolean isPlayerAvailable(Player player) {
        return player != null && player.canPlay();
    }

    protected void prepareMatch() {
        startMatch();
    }

    protected void beforePeriod(int period) {
        if (period > 1) {
            performHalftimeChanges();
        }
    }

    protected void performHalftimeChanges() {

        if (homeTeam instanceof FootballTeam ht) {
            performTeamHalftimeChanges(ht, homeScore, awayScore, true);
        }
        if (awayTeam instanceof FootballTeam at) {
            performTeamHalftimeChanges(at, awayScore, homeScore, false);
        }
    }

    protected void performTeamHalftimeChanges(FootballTeam team, int teamScore, int oppScore, boolean isHome) {
        FootballCoach coach = (FootballCoach) team.getCoach();
        if (coach == null) return;

        Formation newFormation = coach.selectHalftimeFormation(team, homeScore, awayScore, isHome);
        if (newFormation != null && team.getFormation() != null) {
            notifyObservers(String.format("🔄 %s formasyon değiştirdi: %s → %s",
                    team.getName(), team.getFormation().getName(), newFormation.getName()));
            team.setupLineup(newFormation, team.getCurrentTactic());
        }

        int changes = random.nextInt(3);
        for (int i = 0; i < changes; i++) {
            performPlayerSubstitution(team);
        }
    }

    protected void performPlayerSubstitution(FootballTeam team) {
        Player[] lineup = team.getLineup();
        List<Player> subs = team.getSubstitutes();

        if (subs.isEmpty()) return;

        int worstIndex = -1;
        double worstRating = Double.MAX_VALUE;
        for (int i = 0; i < lineup.length && lineup[i] != null; i++) {
            double rating = lineup[i].calculatePerformance();
            if (rating < worstRating) {
                worstRating = rating;
                worstIndex = i;
            }
        }

        if (worstIndex >= 0) {
            Player worstPlayer = lineup[worstIndex];
            Player substitute = subs.get(random.nextInt(subs.size()));

            lineup[worstIndex] = substitute;
            subs.remove(substitute);
            subs.add(worstPlayer);

            notifyObservers(String.format("🔄 Oyuncu değişikliği - Çıkan: %s, Giren: %s",
                    worstPlayer.getName(), substitute.getName()));
        }
    }

    protected void afterPeriod(int period) {

    }

    protected void afterMatch() {
        applyPostMatchInjuries();
    }

    protected abstract void simulateOtherEvents(int minute, int period);

    protected void applyPostMatchInjuries() {
        applyTeamInjuries(homeTeam);
        applyTeamInjuries(awayTeam);
    }

    protected void applyTeamInjuries(Team team) {
        if (!(team instanceof FootballTeam ft)) return;

        int injuryCount = random.nextInt(3);
        if (injuryCount == 0) return;

        Player[] squad = ft.getSquad();
        List<Player> eligiblePlayers = new ArrayList<>();

        for (Player p : squad) {
            if (p != null && !p.isInjured() && random.nextDouble() < 0.3) {
                eligiblePlayers.add(p);
            }
        }

        for (int i = 0; i < injuryCount && !eligiblePlayers.isEmpty(); i++) {
            Player injured = eligiblePlayers.remove(random.nextInt(eligiblePlayers.size()));
            int duration = 1 + random.nextInt(5);
            injured.setInjured(duration);

            notifyObservers(String.format("🏥 %s (%s) %d maç sakatlandı",
                    injured.getName(), team.getName(), duration));
        }
    }

    protected String generatePeriodSummary(int period) {
        return String.format("Devre %d Sonucu: %s %d-%d %s",
                period, homeTeam.getName(), homeScore, awayScore, awayTeam.getName());
    }

    protected void recordResults() {
        boolean homeWin = homeScore > awayScore;
        boolean draw = homeScore == awayScore;

        if (homeTeam instanceof FootballTeam ht) {
            ht.recordMatchResult(homeScore, awayScore, homeWin, draw);
        }
        if (awayTeam instanceof FootballTeam at) {
            at.recordMatchResult(awayScore, homeScore, !homeWin && !draw, draw);
        }
    }

    protected Player getRandomFieldPlayer(Team team) {
        Player[] lineup = (team instanceof FootballTeam ft) ? ft.getLineup() : null;
        if (lineup == null) return null;

        List<Player> available = new ArrayList<>();
        for (Player p : lineup) {
            if (p != null && p.canPlay()) {
                available.add(p);
            }
        }

        return available.isEmpty() ? null : available.get(random.nextInt(available.size()));
    }

    public List<String> getMatchEvents() {
        return matchEvents;
    }

    public List<Player> getGoalScorers() {
        return goalScorers;
    }

    public void setGoalChancePerMinute(double chance) {
        this.goalChancePerMinute = chance;
    }

    public void setInjuryChancePerMinute(double chance) {
        this.injuryChancePerMinute = chance;
    }

    public void setHomeAdvantageBonus(double bonus) {
        this.homeAdvantageBonus = bonus;
    }

    public void setTacticAggressivenessWeight(double weight) {
        this.tacticAggressivenessWeight = weight;
    }
}
}