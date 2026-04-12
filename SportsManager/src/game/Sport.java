package game;

public interface Sport {
    
    String getName();
    
    int getPlayerCount();
    
    int getSubstituteCount();
    
    int getMatchPeriods();
    
    int getPeriodDuration();
    
    int getMinimumScore();
    
    int getMaximumScore();
    
    int getWinPoints();
    
    int getDrawPoints();
    
    int getLossPoints();
    
    String getTiebreakerRule();
    
    String getInjuryRule();
    
    Position[] getValidPositions();
    
    Tactic[] getValidTactics();
    
    FormationFactory getFormationFactory();
    
    boolean isValidMatchResult(int homeScore, int awayScore);
}
