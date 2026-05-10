package game;

public interface PlayerStats extends java.io.Serializable {
    
    String getPlayerId();
    
    String getPlayerName();
    
    double getPerformanceRating();
    
    String getSummary();
}
