package game;

public class FootballMatch extends MatchSimulationEngine {

    private String playerControlledTeamId = null;

    public FootballMatch(String id, Team homeTeam, Team awayTeam, java.time.LocalDateTime matchDate) {
        super(id, homeTeam, awayTeam, new FootballSport(), matchDate);
    }

    public void setPlayerControlledTeamId(String id) {
        this.playerControlledTeamId = id;
    }

    public java.util.List<String> simulateFirstHalf() {
        matchEvents.clear();
        prepareMatch();       
        currentPeriod = 1;
        beforePeriod(1);
        simulatePeriod(1);    
        afterPeriod(1);
        periodSummaries.add(generatePeriodSummary(1));
        return new java.util.ArrayList<>(matchEvents);
    }

    public java.util.List<String> simulateSecondHalf() {
        int startIdx = matchEvents.size();
        currentPeriod = 2;
        beforePeriod(2);      
        simulatePeriod(2);
        afterPeriod(2);
        recordResults();
        afterMatch();
        finishMatch();
        return new java.util.ArrayList<>(matchEvents.subList(startIdx, matchEvents.size()));
    }

    @Override
    protected void performHalftimeChanges() {
        if (homeTeam instanceof FootballTeam ht &&
                !homeTeam.getId().equals(playerControlledTeamId)) {
            performTeamHalftimeChanges(ht, homeScore, awayScore, true);
        }
        if (awayTeam instanceof FootballTeam at &&
                !awayTeam.getId().equals(playerControlledTeamId)) {
            performTeamHalftimeChanges(at, awayScore, homeScore, false);
        }
    }

    @Override
    public void addScore(Team team, Player player) {
        if (team.getId().equals(homeTeam.getId())) {
            homeScore++;
        } else if (team.getId().equals(awayTeam.getId())) {
            awayScore++;
        }

        if (player instanceof FootballPlayer fp) {
            fp.addGoal();
        }
        goalScorers.add(player);

        String event = String.format("⚽ %d' - GOL! %s (%s) | Skor: %s %d-%d %s",
            currentMinute,
            player.getName(),
            team.getName(),
            homeTeam.getName(),
            homeScore,
            awayScore,
            awayTeam.getName()
        );
        matchEvents.add(event);
        notifyObservers(event);
    }

    @Override
    protected void simulateOtherEvents(int minute, int period) {
        
        if (random.nextDouble() < 0.005) {
            Team cardTeam = random.nextBoolean() ? homeTeam : awayTeam;
            Player cardPlayer = getRandomFieldPlayer(cardTeam);
            if (cardPlayer != null && cardPlayer.canPlay()) {
                cardPlayer.addYellowCard();
                String cardEvent = String.format("🟨 %d' - Sarı Kart: %s (%s)",
                    minute, cardPlayer.getName(), cardTeam.getName());
                matchEvents.add(cardEvent);
                notifyObservers(cardEvent);

                if (cardPlayer.getYellowCards() >= 2) {
                    cardPlayer.addRedCard();
                    String redEvent = String.format("🟥 %d' - Kırmızı Kart (2. Sarı): %s (%s)",
                        minute, cardPlayer.getName(), cardTeam.getName());
                    matchEvents.add(redEvent);
                    notifyObservers(redEvent);
                }
            }
        }
    }

    @Override
    public MatchResult getMatchResult() {
        return new FootballMatchResult(this);
    }

    private static class FootballMatchResult implements MatchResult {

        private final FootballMatch match;

        public FootballMatchResult(FootballMatch match) {
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
            if (match.homeScore > match.awayScore) {
                return match.homeTeam;
            } else if (match.awayScore > match.homeScore) {
                return match.awayTeam;
            }
            return null; 
        }

        @Override
        public MatchOutcome getOutcome() {
            if (match.homeScore > match.awayScore) {
                return MatchOutcome.HOME_WIN;
            } else if (match.awayScore > match.homeScore) {
                return MatchOutcome.AWAY_WIN;
            }
            return MatchOutcome.DRAW;
        }

        @Override
        public Player getManOfTheMatch() {
            Player best = null;
            double bestPerf = 0;

            for (Team team : new Team[]{match.homeTeam, match.awayTeam}) {
                for (Player p : team.getSquad()) {
                    if (p != null) {
                        double perf = p.calculatePerformance();
                        if (perf > bestPerf) {
                            bestPerf = perf;
                            best = p;
                        }
                    }
                }
            }
            return best;
        }

        @Override
        public String getSummary() {
            return String.format(
                "%s %d-%d %s | Kazanan: %s | MOM: %s",
                match.homeTeam.getName(),
                match.homeScore,
                match.awayScore,
                match.awayTeam.getName(),
                getWinner() != null ? getWinner().getName() : "Berabere",
                getManOfTheMatch() != null ? getManOfTheMatch().getName() : "-"
            );
        }
    }
}
