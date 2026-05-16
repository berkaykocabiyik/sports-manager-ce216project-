package game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VolleyballMatch extends Match {

    private static final int SETS_TO_WIN = 3;
    private final List<String> matchEvents = new ArrayList<>();
    private final List<int[]> setScores = new ArrayList<>();
    private final Random random = new Random();
    private int homeTotalRallyPoints;
    private int awayTotalRallyPoints;

    public VolleyballMatch(String id, Team homeTeam, Team awayTeam, LocalDateTime matchDate) {
        super(id, homeTeam, awayTeam, new VolleyballSport(), matchDate);
    }

    @Override
    public void addScore(Team team, Player player) {
        if (team.getId().equals(homeTeam.getId())) {
            homeScore++;
        } else if (team.getId().equals(awayTeam.getId())) {
            awayScore++;
        }
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            volleyballPlayer.addSpike();
        }
    }

    @Override
    public void simulateMatch() {
        if (status != MatchStatus.SCHEDULED) {
            return;
        }

        startMatch();
        matchEvents.add("Maç başladı: " + homeTeam.getName() + " - " + awayTeam.getName());

        while (homeScore < SETS_TO_WIN && awayScore < SETS_TO_WIN) {
            currentPeriod = setScores.size() + 1;
            int targetScore = currentPeriod == 5 ? 15 : 25;
            int[] score = simulateSet(targetScore);
            setScores.add(score);
            homeTotalRallyPoints += score[0];
            awayTotalRallyPoints += score[1];

            if (score[0] > score[1]) {
                homeScore++;
                addEvent(String.format("%d. set: %s %d-%d kazandı",
                    currentPeriod, homeTeam.getName(), score[0], score[1]));
            } else {
                awayScore++;
                addEvent(String.format("%d. set: %s %d-%d kazandı",
                    currentPeriod, awayTeam.getName(), score[1], score[0]));
            }
        }

        recordResults();
        applyPostMatchInjuries();
        finishMatch();
        addEvent(String.format("Maç bitti: %s %d-%d %s",
            homeTeam.getName(), homeScore, awayScore, awayTeam.getName()));
        Player manOfTheMatch = getMatchResult().getManOfTheMatch();
        if (manOfTheMatch != null) {
            addEvent("Maçın oyuncusu: " + manOfTheMatch.getName());
        }
    }

    @Override
    public MatchResult getMatchResult() {
        return new VolleyballMatchResult(this);
    }

    private int[] simulateSet(int targetScore) {
        int homeSetPoints = 0;
        int awaySetPoints = 0;

        while ((homeSetPoints < targetScore && awaySetPoints < targetScore)
                || Math.abs(homeSetPoints - awaySetPoints) < 2) {
            currentMinute = homeSetPoints + awaySetPoints + 1;
            boolean homePoint = random.nextDouble() < calculateHomePointChance();

            if (homePoint) {
                homeSetPoints++;
                awardPointStats(homeTeam, awayTeam);
            } else {
                awaySetPoints++;
                awardPointStats(awayTeam, homeTeam);
            }
        }

        return new int[] {homeSetPoints, awaySetPoints};
    }

    private double calculateHomePointChance() {
        double homePower = calculatePointPower(homeTeam, true);
        double awayPower = calculatePointPower(awayTeam, false);
        double total = homePower + awayPower;
        if (total <= 0) {
            return 0.5;
        }
        double chance = homePower / total;
        return Math.max(0.25, Math.min(0.75, chance));
    }

    private double calculatePointPower(Team team, boolean home) {
        double power = team.calculateTeamStrength();
        if (home) {
            power += 0.25;
        }
        if (team.getCurrentTactic() != null) {
            power += team.getCurrentTactic().getAggressivenessLevel() * 0.04;
            power += team.getCurrentTactic().getDefenseStrength() * 0.025;
        }
        if (team.getFormation() != null) {
            power += team.getFormation().getOffensiveStrength() * 0.025;
            power += team.getFormation().getDefensiveStrength() * 0.025;
        }
        return Math.max(1.0, power);
    }

    private void awardPointStats(Team scoringTeam, Team concedingTeam) {
        Player scorer = randomLineupPlayer(scoringTeam);
        if (scorer instanceof VolleyballPlayer volleyballPlayer) {
            double roll = random.nextDouble();
            if (roll < 0.08) {
                volleyballPlayer.addAce();
            } else if (roll < 0.22) {
                volleyballPlayer.addBlock();
            } else {
                volleyballPlayer.addSpike();
            }
        }

        Player defender = randomLineupPlayer(concedingTeam);
        if (defender instanceof VolleyballPlayer volleyballPlayer) {
            if (random.nextDouble() < 0.65) {
                volleyballPlayer.addDig();
            } else {
                volleyballPlayer.addError();
            }
        }
    }

    private Player randomLineupPlayer(Team team) {
        List<Player> available = new ArrayList<>();
        for (Player player : team.getLineup()) {
            if (player != null && player.canPlay()) {
                available.add(player);
            }
        }
        if (available.isEmpty()) {
            for (Player player : team.getSquad()) {
                if (player != null && player.canPlay()) {
                    available.add(player);
                }
            }
        }
        return available.isEmpty() ? null : available.get(random.nextInt(available.size()));
    }

    private void recordResults() {
        if (homeTeam instanceof VolleyballTeam home) {
            home.recordVolleyballResult(homeScore, awayScore, homeTotalRallyPoints,
                awayTotalRallyPoints, homeScore > awayScore);
        }
        if (awayTeam instanceof VolleyballTeam away) {
            away.recordVolleyballResult(awayScore, homeScore, awayTotalRallyPoints,
                homeTotalRallyPoints, awayScore > homeScore);
        }
    }

    private void applyPostMatchInjuries() {
        applyTeamInjuries(homeTeam);
        applyTeamInjuries(awayTeam);
    }

    private void applyTeamInjuries(Team team) {
        int injuryCount = random.nextInt(3);
        if (injuryCount == 0) {
            return;
        }

        List<Player> eligible = new ArrayList<>();
        for (Player player : team.getSquad()) {
            if (player != null && !player.isInjured()) {
                eligible.add(player);
            }
        }

        for (int i = 0; i < injuryCount && !eligible.isEmpty(); i++) {
            Player injured = eligible.remove(random.nextInt(eligible.size()));
            int duration = 1 + random.nextInt(5);
            injured.setInjured(duration);
            addEvent(String.format("Sakatlık: %s (%s), %d maç yok",
                injured.getName(), team.getName(), duration));
        }
    }

    private void addEvent(String event) {
        matchEvents.add(event);
        notifyObservers(event);
    }

    public List<String> getMatchEvents() {
        return new ArrayList<>(matchEvents);
    }

    public List<int[]> getSetScores() {
        return new ArrayList<>(setScores);
    }

    public String getFormattedSetScores() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < setScores.size(); i++) {
            if (i > 0) builder.append(", ");
            int[] set = setScores.get(i);
            builder.append(set[0]).append("-").append(set[1]);
        }
        return builder.toString();
    }

    public List<String> getInjuryEvents() {
        List<String> injuries = new ArrayList<>();
        for (String event : matchEvents) {
            if (event.startsWith("Sakatlık:")) {
                injuries.add(event);
            }
        }
        return injuries;
    }

    public String getTeamStatSummary(Team team) {
        int spikes = 0;
        int blocks = 0;
        int aces = 0;
        int digs = 0;
        int errors = 0;

        for (Player player : team.getSquad()) {
            if (player instanceof VolleyballPlayer volleyballPlayer) {
                spikes += volleyballPlayer.getSpikes();
                blocks += volleyballPlayer.getBlocks();
                aces += volleyballPlayer.getAces();
                digs += volleyballPlayer.getDigs();
                errors += volleyballPlayer.getErrors();
            }
        }

        return String.format("Smaç: %d | Blok: %d | Ace: %d | Defans: %d | Hata: %d",
            spikes, blocks, aces, digs, errors);
    }

    public int getHomeTotalRallyPoints() {
        return homeTotalRallyPoints;
    }

    public int getAwayTotalRallyPoints() {
        return awayTotalRallyPoints;
    }

    private static class VolleyballMatchResult implements MatchResult {

        private final VolleyballMatch match;

        VolleyballMatchResult(VolleyballMatch match) {
            this.match = match;
        }

        @Override
        public Team getHomeTeam() {
            return match.homeTeam;
        }

        @Override
        public Team getAwayTeam() {
            return match.awayTeam;
        }

        @Override
        public int getHomeScore() {
            return match.homeScore;
        }

        @Override
        public int getAwayScore() {
            return match.awayScore;
        }

        @Override
        public Team getWinner() {
            return match.homeScore > match.awayScore ? match.homeTeam : match.awayTeam;
        }

        @Override
        public MatchOutcome getOutcome() {
            return match.homeScore > match.awayScore
                ? MatchOutcome.HOME_WIN
                : MatchOutcome.AWAY_WIN;
        }

        @Override
        public Player getManOfTheMatch() {
            Player best = null;
            double bestPerformance = 0;
            for (Team team : new Team[] {match.homeTeam, match.awayTeam}) {
                for (Player player : team.getSquad()) {
                    if (player != null && player.calculatePerformance() > bestPerformance) {
                        best = player;
                        bestPerformance = player.calculatePerformance();
                    }
                }
            }
            return best;
        }

        @Override
        public String getSummary() {
            return String.format("%s %d-%d %s | Setler: %s | Kazanan: %s",
                match.homeTeam.getName(),
                match.homeScore,
                match.awayScore,
                match.awayTeam.getName(),
                formatSets(),
                getWinner().getName());
        }

        private String formatSets() {
            return match.getFormattedSetScores();
        }
    }
}
