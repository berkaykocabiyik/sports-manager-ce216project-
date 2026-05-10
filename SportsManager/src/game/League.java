package game;

public abstract class League implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String name;
    protected Sport sport;
    protected Team[] teams;
    protected Standings standings;
    protected Fixture fixture;
    protected transient java.util.List<LeagueObserver> observers = new java.util.ArrayList<>();
    
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

    public java.util.List<Match> playWeek(int week) {
        if (fixture == null) {
            generateFixture();
        }
        java.util.List<Match> results = new java.util.ArrayList<>();
        for (Match match : fixture.getWeekMatches(week)) {
            if (match.getStatus() == MatchStatus.SCHEDULED) {
                playMatch(match);
                results.add(match);
            }
        }
        return results;
    }

    public java.util.List<Team> getActiveTeams() {
        java.util.List<Team> activeTeams = new java.util.ArrayList<>();
        for (Team team : teams) {
            if (team != null) {
                activeTeams.add(team);
            }
        }
        return activeTeams;
    }

    public int getTeamCount() {
        return getActiveTeams().size();
    }

    public java.util.List<Match> getPlayedMatches() {
        return java.util.List.of();
    }
    
    public Standings getStandings() {
        return standings;
    }
    
    public void addObserver(LeagueObserver observer) {
        ensureObservers();
        observers.add(observer);
    }
    
    public void removeObserver(LeagueObserver observer) {
        ensureObservers();
        observers.remove(observer);
    }
    
    protected void notifyObservers(String event) {
        ensureObservers();
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
        ensureObservers();
        observers.clear();
    }

    private void ensureObservers() {
        if (observers == null) {
            observers = new java.util.ArrayList<>();
        }
    }
}
