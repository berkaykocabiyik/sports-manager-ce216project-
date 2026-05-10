package game;

public interface Tactic extends java.io.Serializable {
    
    String getName();
    
    String getDescription();
    
    String getSportType();
    
    int getAggressivenessLevel();
    
    int getDefenseStrength();
    
    String[] getAdvantages();
    
    String[] getDisadvantages();
}
