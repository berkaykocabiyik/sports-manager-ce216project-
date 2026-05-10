package game;

public interface MatchResult extends java.io.Serializable {
    
    Team getHomeTeam();
    
    Team getAwayTeam();
    
    int getHomeScore();
    
    int getAwayScore();
    
    Team getWinner();
    
    MatchOutcome getOutcome();
    
    Player getManOfTheMatch();
    
    String getSummary();
}
