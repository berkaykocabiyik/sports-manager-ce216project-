package game;

public interface Tactic {
    
    String getName();
    
    String getDescription();
    
    String getSportType();
    
    int getAggressivenessLevel();
    
    int getDefenseStrength();
    
    String[] getAdvantages();
    
    String[] getDisadvantages();
}
