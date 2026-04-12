package game;

public interface MatchResult {

    Team getHomeTeam();

    Team getAwayTeam();

    int getHomeScore();

    int getAwayScore();

    Team getWinner();

    MatchOutcome getOutcome();

    Player getManOfTheMatch();

    String getSummary();
}