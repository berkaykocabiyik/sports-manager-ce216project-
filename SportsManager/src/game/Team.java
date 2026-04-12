package game;

public abstract class Team {
    
    protected String id;
    protected String name;
    protected String city;
    protected Coach coach;
    protected Player[] squad; 
    protected Player[] lineup; 
    protected Formation formation;
    protected Tactic currentTactic;
    protected int totalWins;
    protected int totalDraws;
    protected int totalLosses;
    protected int goalsFor;
    protected int goalsAgainst;
    
    public Team(String id, String name, String city, Coach coach) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.coach = coach;
        this.squad = new Player[100]; 
        this.lineup = new Player[20]; 
        this.totalWins = 0;
        this.totalDraws = 0;
        this.totalLosses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }
    
    public abstract void addPlayer(Player player);
    
    public abstract void removePlayer(String playerId);
    
    public abstract double calculateTeamStrength();
    
    public abstract void setupLineup(Formation formation, Tactic tactic);
    
    public abstract TeamStats getStats();
    
    public void recordMatchResult(int goalsFor, int goalsAgainst, boolean isWin, boolean isDraw) {
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        
        if (isWin) {
            totalWins++;
        } else if (isDraw) {
            totalDraws++;
        } else {
            totalLosses++;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCity() {
        return city;
    }
    
    public Coach getCoach() {
        return coach;
    }
    
    public void setCoach(Coach coach) {
        this.coach = coach;
    }
    
    public Player[] getSquad() {
        return squad;
    }
    
    public Player[] getLineup() {
        return lineup;
    }
    
    public Formation getFormation() {
        return formation;
    }
    
    public Tactic getCurrentTactic() {
        return currentTactic;
    }
    
    public int getTotalWins() {
        return totalWins;
    }
    
    public int getTotalDraws() {
        return totalDraws;
    }
    
    public int getTotalLosses() {
        return totalLosses;
    }
    
    public int getGoalsFor() {
        return goalsFor;
    }
    
    public int getGoalsAgainst() {
        return goalsAgainst;
    }
    
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
}
