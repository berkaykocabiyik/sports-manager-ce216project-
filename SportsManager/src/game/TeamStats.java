package game;

public interface TeamStats extends java.io.Serializable {
    
    String getTeamName();
    
    int getMatchesPlayed();
    
    int getPoints();
    
    int getGoalsFor();
    
    int getGoalsAgainst();
    
    int getGoalDifference();
    
    double getFormRating();
    
    String getSummary();
}
