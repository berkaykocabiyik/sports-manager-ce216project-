package game;

public interface FormationFactory extends java.io.Serializable {
    
    Formation createDefensiveFormation();
    
    Formation createBalancedFormation();
    
    Formation createOffensiveFormation();
    
    Formation createFormation(String name);
    
    Formation[] getAllFormations();
}
