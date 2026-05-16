package game;

public class VolleyballPlayer extends Player {

    private int overallRating;
    private int spikes;
    private int blocks;
    private int aces;
    private int digs;
    private int errors;

    public VolleyballPlayer(String id, String name, int age, Position position, int overallRating) {
        super(id, name, age, position, overallRating / 10.0);
        this.overallRating = Math.max(40, Math.min(99, overallRating));
        this.rating = this.overallRating / 10.0;
    }

    @Override
    public double calculatePerformance() {
        double positive = spikes * 0.18 + blocks * 0.25 + aces * 0.35 + digs * 0.08;
        double negative = errors * 0.15;
        return Math.max(1.0, Math.min(10.0, rating + positive - negative));
    }

    @Override
    public PlayerStats getStats() {
        return new VolleyballPlayerStats(this);
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void train(int amount) {
        setOverallRating(overallRating + amount);
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = Math.max(40, Math.min(99, overallRating));
        this.rating = this.overallRating / 10.0;
    }

    public void addSpike() {
        spikes++;
    }

    public void addBlock() {
        blocks++;
    }

    public void addAce() {
        aces++;
    }

    public void addDig() {
        digs++;
    }

    public void addError() {
        errors++;
    }

    public int getSpikes() {
        return spikes;
    }

    public int getBlocks() {
        return blocks;
    }

    public int getAces() {
        return aces;
    }

    public int getDigs() {
        return digs;
    }

    public int getErrors() {
        return errors;
    }

    private static class VolleyballPlayerStats implements PlayerStats {

        private final VolleyballPlayer player;

        VolleyballPlayerStats(VolleyballPlayer player) {
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
                "%s (%s) - OVR: %d | Smaç: %d | Blok: %d | Ace: %d | Defans: %d",
                player.getName(),
                player.getPosition().getDisplayName(),
                player.getOverallRating(),
                player.getSpikes(),
                player.getBlocks(),
                player.getAces(),
                player.getDigs()
            );
        }
    }
}
