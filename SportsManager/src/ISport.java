import java.util.List;

public interface ISport {
    String getName();                      	// "Football", "Volleyball"
    int getPlayersPerTeam();               	// Starting lineup size
    int getSubstitutesPerTeam();
    int getPeriodsPerMatch();              	// quarters, halves, sets
    String getPeriodName();                	// "Quarter", "Half", "Set"
    int getUnlimitedSubstitutions();       	// -1 = unlimited
    PointSystem getPointSystem();              // Win/Draw/Loss points
    TiebreakerRules getTiebreakerRules();
    List<String> getPositions();
    List<String> getAvailableTactics();
    IPlayer createPlayer(String name, String position);
    ITeam createTeam(String name, String logoPath);
    IMatchEngine createMatchEngine();
    List<String> getPlayerAttributeNames();	// e.g. "Speed", "Shooting"
}
