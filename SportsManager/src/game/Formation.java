package game;

public interface Formation extends java.io.Serializable {
    
    String getName();
    
    String getDescription();
    
    Position[] getRequiredPositions();
    
    int getPositionCount(Position position);
    
    int getTotalPlayerCount();
    
    int getOffensiveStrength();
    
    int getDefensiveStrength();
}
