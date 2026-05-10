package game;

public interface Fixture extends java.io.Serializable {
    
    String getName();
    
    Match[] getAllMatches();
    
    Match[] getWeekMatches(int week);
    
    Match[] getTeamMatches(Team team);
    
    Match getNextMatch(Team team);
    
    int getTotalWeeks();
    
    java.time.LocalDate getStartDate();
    
    java.time.LocalDate getEndDate();
}
