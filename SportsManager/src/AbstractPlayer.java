public abstract class AbstractPlayer implements IPlayer {
    protected String name;
    protected String position;
    protected Map<String, Integer> attributes;
    private int injuryGamesRemaining = 0;

    // Shared concrete implementation
    @Override public boolean isInjured() { return injuryGamesRemaining > 0; }
    @Override public void applyInjury(int games) { injuryGamesRemaining = games; }
    @Override public void decrementInjury() {
        if (injuryGamesRemaining > 0) injuryGamesRemaining--;
    }

    // Sport-specific, must be implemented by subclass
    @Override public abstract int getOverallRating();
}
