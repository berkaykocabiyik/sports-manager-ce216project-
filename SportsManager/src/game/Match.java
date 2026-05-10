package game;

public abstract class Match implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected Team homeTeam;
    protected Team awayTeam;
    protected int homeScore;
    protected int awayScore;
    protected MatchStatus status; 
    protected java.time.LocalDateTime matchDate;
    protected int currentPeriod;
    protected int currentMinute;
    protected Sport sport;
    
    protected transient java.util.List<MatchObserver> observers = new java.util.ArrayList<>();
    
    public Match(String id, Team homeTeam, Team awayTeam, Sport sport, java.time.LocalDateTime matchDate) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.sport = sport;
        this.matchDate = matchDate;
        this.homeScore = 0;
        this.awayScore = 0;
        this.status = MatchStatus.SCHEDULED;
        this.currentPeriod = 0;
        this.currentMinute = 0;
    }
    
    public void startMatch() {
        this.status = MatchStatus.ONGOING;
        this.currentPeriod = 1;
        notifyObservers("Maç başladı!");
    }
    
    public void finishMatch() {
        this.status = MatchStatus.FINISHED;
        notifyObservers("Maç bitti!");
    }
    
    public abstract void addScore(Team team, Player player);
    
    public abstract void simulateMatch();
    
    public abstract MatchResult getMatchResult();
    
    public void addObserver(MatchObserver observer) {
        ensureObservers();
        observers.add(observer);
    }
    
    public void removeObserver(MatchObserver observer) {
        ensureObservers();
        observers.remove(observer);
    }
    
    protected void notifyObservers(String event) {
        ensureObservers();
        for (MatchObserver observer : observers) {
            observer.update(this, event);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public Team getHomeTeam() {
        return homeTeam;
    }
    
    public Team getAwayTeam() {
        return awayTeam;
    }
    
    public int getHomeScore() {
        return homeScore;
    }
    
    public int getAwayScore() {
        return awayScore;
    }
    
    public MatchStatus getStatus() {
        return status;
    }
    
    public java.time.LocalDateTime getMatchDate() {
        return matchDate;
    }
    
    public int getCurrentPeriod() {
        return currentPeriod;
    }
    
    public int getCurrentMinute() {
        return currentMinute;
    }
    
    public Sport getSport() {
        return sport;
    }

    private void ensureObservers() {
        if (observers == null) {
            observers = new java.util.ArrayList<>();
        }
    }
}
