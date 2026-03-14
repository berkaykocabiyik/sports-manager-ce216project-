public interface ILeague {
    ISport getSport();
    List<ITeam> getTeams();
    Fixture getFixture();
    Standings getStandings();
    int getCurrentWeek();
    void advanceWeek();
    boolean isLeagueOver();
    ITeam getChampion();
}
