package game;

public interface Standings extends java.io.Serializable {
    
    Team[] getRankedTeams();
    
    int getTeamRank(Team team);
    
    int getTeamPoints(Team team);
    
    int getTeamMatchesPlayed(Team team);
    
    int getTeamWins(Team team);
    
    int getTeamDraws(Team team);
    
    int getTeamLosses(Team team);
    
    String getTableDisplay();
    
    void printTable();
}
