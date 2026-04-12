package game;

public interface FormationFactory {

    Formation createDefensiveFormation();

    Formation createBalancedFormation();

    Formation createOffensiveFormation();

    Formation createFormation(String name);

    Formation[] getAllFormations();
}