package game;

public interface Formation {

    String getName();

    String getDescription();

    Position[] getRequiredPositions();

    int getPositionCount(Position position);

    int getTotalPlayerCount();

    int getOffensiveStrength();

    int getDefensiveStrength();
}