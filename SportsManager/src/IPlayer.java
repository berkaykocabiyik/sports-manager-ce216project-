import java.util.Map;

public interface IPlayer {
    String getName();
    String getPosition();
    int getOverallRating();
    Map<String, Integer> getAttributes();  	// sport-specific attributes
    boolean isInjured();
    int getInjuryGamesRemaining();
    void applyInjury(int games);
    void decrementInjury();                	// called after each match
    void train(String attributeName, int amount);
}
