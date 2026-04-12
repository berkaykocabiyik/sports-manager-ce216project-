package game;

public class FootballCoach extends Coach {

    private static final String[] FORMATION_NAMES = {"4-4-2", "4-3-3", "3-5-2"};

    public FootballCoach(String id, String name, int experience, double coachingRating, String specialty) {
        super(id, name, experience, coachingRating, specialty);
    }

    @Override
    public Tactic selectTactic(Team team, Team opponent) {
        double teamStrength = team.calculateTeamStrength();
        double opponentStrength = opponent.calculateTeamStrength();

        if (opponentStrength > teamStrength + 1) {
            return new FootballSport.FootballDefensiveTactic();
        } else if (opponentStrength < teamStrength - 1) {
            return new FootballSport.FootballOffensiveTactic();
        } else {
            return new FootballSport.FootballBalancedTactic();
        }
    }

    @Override
    public Formation selectFormation(Team team) {
        FootballFormationFactory factory = new FootballFormationFactory();
        double teamStrength = team.calculateTeamStrength();

        if (teamStrength > 8) {

            return factory.createFormation(FORMATION_NAMES[2]);
        } else if (teamStrength < 5) {

            return factory.createFormation(FORMATION_NAMES[0]);
        } else {

            return factory.createFormation(FORMATION_NAMES[1]);
        }
    }

    public Formation selectHalftimeFormation(Team team, int homeScore, int awayScore, boolean isHome) {
        FootballFormationFactory factory = new FootballFormationFactory();
        int teamScore = isHome ? homeScore : awayScore;
        int opponentScore = isHome ? awayScore : homeScore;

        if (teamScore < opponentScore) {

            return factory.createOffensiveFormation();
        } else if (teamScore > opponentScore) {

            return factory.createDefensiveFormation();
        }
        return null;
    }

    public Tactic selectHalftimeTactic(int homeScore, int awayScore, boolean isHome) {
        int teamScore = isHome ? homeScore : awayScore;
        int opponentScore = isHome ? awayScore : homeScore;

        if (teamScore < opponentScore) {
            return new FootballSport.FootballOffensiveTactic();
        } else if (teamScore > opponentScore) {
            return new FootballSport.FootballDefensiveTactic();
        }
        return null;
    }

    @Override
    public void setupLineup(Team team, Formation formation, Tactic tactic) {
        if (!(team instanceof FootballTeam footballTeam)) {
            return;
        }

        Player[] squad = footballTeam.getSquad();
        Player[] lineup = new Player[11];
        int lineupIndex = 0;

        int needGK = formation.getPositionCount(Position.GOALKEEPER);
        int needDEF = formation.getPositionCount(Position.DEFENDER);
        int needMID = formation.getPositionCount(Position.MIDFIELDER);
        int needFWD = formation.getPositionCount(Position.FORWARD);

        java.util.List<Player> gks = new java.util.ArrayList<>();
        java.util.List<Player> defs = new java.util.ArrayList<>();
        java.util.List<Player> mids = new java.util.ArrayList<>();
        java.util.List<Player> fwds = new java.util.ArrayList<>();

        for (Player p : squad) {
            if (p != null && p.canPlay()) {
                switch (p.getPosition()) {
                    case GOALKEEPER -> gks.add(p);
                    case DEFENDER -> defs.add(p);
                    case MIDFIELDER -> mids.add(p);
                    case FORWARD -> fwds.add(p);
                    default -> {}
                }
            }
        }

        java.util.Comparator<Player> byRating = (a, b) -> Double.compare(b.getRating(), a.getRating());
        gks.sort(byRating);
        defs.sort(byRating);
        mids.sort(byRating);
        fwds.sort(byRating);

        for (int i = 0; i < needGK && i < gks.size(); i++) {
            lineup[lineupIndex++] = gks.get(i);
        }
        for (int i = 0; i < needDEF && i < defs.size(); i++) {
            lineup[lineupIndex++] = defs.get(i);
        }
        for (int i = 0; i < needMID && i < mids.size(); i++) {
            lineup[lineupIndex++] = mids.get(i);
        }
        for (int i = 0; i < needFWD && i < fwds.size(); i++) {
            lineup[lineupIndex++] = fwds.get(i);
        }

        if (lineupIndex < 11) {
            for (Player p : squad) {
                if (p != null && p.canPlay() && !isInLineup(lineup, p)) {
                    lineup[lineupIndex++] = p;
                    if (lineupIndex >= 11) break;
                }
            }
        }

        for (int i = 0; i < lineup.length && i < team.getLineup().length; i++) {
            team.getLineup()[i] = lineup[i];
        }
    }

    private boolean isInLineup(Player[] lineup, Player player) {
        for (Player p : lineup) {
            if (p != null && p.getId().equals(player.getId())) {
                return true;
            }
        }
        return false;
    }
}