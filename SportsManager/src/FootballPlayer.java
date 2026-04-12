package game;

public class FootballPlayer extends Player {

    private int overallRating;
    private int goals;
    private int assists;
    private int tackles;
    private int passes;
    private int accuratePasses;

    public FootballPlayer(String id, String name, int age, Position position, int overallRating) {
        super(id, name, age, position, overallRating / 10.0);
        this.overallRating = Math.max(40, Math.min(99, overallRating));
        this.rating = this.overallRating / 10.0;
        this.goals = 0;
        this.assists = 0;
        this.tackles = 0;
        this.passes = 0;
        this.accuratePasses = 0;
    }

    public FootballPlayer(String id, String name, int age, Position position, double rating) {
        super(id, name, age, position, rating);
        this.overallRating = (int) (rating * 10);
        this.goals = 0;
        this.assists = 0;
        this.tackles = 0;
        this.passes = 0;
        this.accuratePasses = 0;
    }

    @Override
    public double calculatePerformance() {
        double baseRating = this.rating;
        double goalContribution = (goals + assists) * 0.5;
        double defenseContribution = tackles * 0.2;
        double passContribution = (passes > 0) ? ((double) accuratePasses / passes) : 0;

        double performance = baseRating + goalContribution + defenseContribution + passContribution;
        return Math.min(10.0, performance);
    }

    @Override
    public PlayerStats getStats() {
        return new FootballPlayerStats(this);
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int ovr) {
        this.overallRating = Math.max(40, Math.min(99, ovr));
        this.rating = this.overallRating / 10.0;
    }

    public int getGoals() {
        return goals;
    }

    public void addGoal() {
        this.goals++;
    }

    public int getAssists() {
        return assists;
    }

    public void addAssist() {
        this.assists++;
    }

    public int getTackles() {
        return tackles;
    }

    public void addTackle() {
        this.tackles++;
    }

    public int getPasses() {
        return passes;
    }

    public int getAccuratePasses() {
        return accuratePasses;
    }

    public void addPass(boolean accurate) {
        this.passes++;
        if (accurate) {
            this.accuratePasses++;
        }
    }

    public int getPassAccuracy() {
        if (passes == 0) return 0;
        return (int) ((double) accuratePasses / passes * 100);
    }

    private static class FootballPlayerStats implements PlayerStats {

        private final FootballPlayer player;

        public FootballPlayerStats(FootballPlayer player) {
            this.player = player;
        }

        @Override
        public String getPlayerId() {
            return player.getId();
        }

        @Override
        public String getPlayerName() {
            return player.getName();
        }

        @Override
        public double getPerformanceRating() {
            return player.calculatePerformance();
        }

        @Override
        public String getSummary() {
            return String.format(
                    "%s (%s) - OVR: %d | Goals: %d | Assists: %d | Tackles: %d | Passes: %d (%d%% accuracy)",
                    player.getName(),
                    player.getPosition().getDisplayName(),
                    player.getOverallRating(),
                    player.getGoals(),
                    player.getAssists(),
                    player.getTackles(),
                    player.getPasses(),
                    player.getPassAccuracy()
            );
        }
    }
}