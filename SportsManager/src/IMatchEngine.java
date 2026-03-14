import java.util.regex.MatchResult;

public interface IMatchEngine {
    MatchResult simulatePeriod(ITeam home, ITeam away, int periodNumber);
    MatchResult getFinalResult();
    List<MatchEvent> getEvents();          	// goals, injuries, etc.
    boolean isMatchOver();
}
