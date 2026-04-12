package game;

public interface TeamStats {
    
    String getTeamName();
    
    int getMatchesPlayed();
    
    int getPoints();
    
    int getGoalsFor();
    
    int getGoalsAgainst();
    
    int getGoalDifference();
    
    double getFormRating();
    
    String getSummary();
}
