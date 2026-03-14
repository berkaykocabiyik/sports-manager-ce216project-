public class LeagueViewController {
    private GameContext ctx;

    public void initialize() {
        ILeague league = ctx.getCurrentLeague();
        Standings s = league.getStandings();
        // TableView'ı doldur
    }
    public void loadStandings() {}
    public void onColumnSorted() {}
}