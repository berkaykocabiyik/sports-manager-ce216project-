package game;

public abstract class Player {

    protected String id;
    protected String name;
    protected int age;
    protected Position position;
    protected double rating; 
    protected boolean isInjured;
    protected int injuredMatchesRemaining; 
    protected int yellowCards;
    protected int redCards;

    public Player(String id, String name, int age, Position position, double rating) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.rating = Math.max(1.0, Math.min(10.0, rating));
        this.isInjured = false;
        this.injuredMatchesRemaining = 0;
        this.yellowCards = 0;
        this.redCards = 0;
    }

    public abstract double calculatePerformance();

    public abstract PlayerStats getStats();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Position getPosition() {
        return position;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = Math.max(1.0, Math.min(10.0, rating));
    }

    public boolean isInjured() {
        return isInjured || injuredMatchesRemaining > 0;
    }

    public void setInjured(boolean injured) {
        isInjured = injured;
        if (!injured) {
            injuredMatchesRemaining = 0;
        }
    }

    public void setInjured(int matches) {
        this.injuredMatchesRemaining = matches;
        this.isInjured = matches > 0;
    }

    public void decrementInjury() {
        if (injuredMatchesRemaining > 0) {
            injuredMatchesRemaining--;
            if (injuredMatchesRemaining == 0) {
                isInjured = false;
            }
        }
    }

    public int getInjuredMatchesRemaining() {
        return injuredMatchesRemaining;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void addYellowCard() {
        this.yellowCards++;
    }

    public void resetYellowCards() {
        this.yellowCards = 0;
    }

    public int getRedCards() {
        return redCards;
    }

    public void addRedCard() {
        this.redCards++;
    }

    public void resetRedCards() {
        this.redCards = 0;
    }

    public boolean canPlay() {
        return !isInjured() && redCards == 0;
    }
}
