public class GameContext {
    private static GameContext instance;
    private ILeague currentLeague;

    private GameContext() {}

    public static GameContext getInstance() { ... }
    public ILeague getCurrentLeague() { ... }
    public void setCurrentLeague(ILeague league) { ... }
}