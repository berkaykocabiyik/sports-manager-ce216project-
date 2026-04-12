package game;

public abstract class League {
    
    protected String id;
    protected String name;
    protected Sport sport;
    protected Team[] teams;
    protected Standings standings;
    protected Fixture fixture;
    protected java.util.List<LeagueObserver> observers = new java.util.ArrayList<>();
    
    public League(String id, String name, Sport sport) {
        this.id = id;
        this.name = name;
        this.sport = sport;
        this.teams = new Team[100]; 
    }
    
    public abstract void addTeam(Team team);
    
    public abstract void removeTeam(String teamId);
    
    public abstract void generateFixture();
    
    public abstract void updateStandings();
    
    public abstract void playMatch(Match match);
    
    public Standings getStandings() {
        return standings;
    }
    
    public void addObserver(LeagueObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(LeagueObserver observer) {
        observers.remove(observer);
    }
    
    protected void notifyObservers(String event) {
        for (LeagueObserver observer : observers) {
            observer.leagueUpdated(this, event);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Sport getSport() {
        return sport;
    }
    
    public Team[] getTeams() {
        return teams;
    }
    
    public Fixture getFixture() {
        return fixture;
    }

    public void clearObservers() {
        observers.clear();
    }
}
