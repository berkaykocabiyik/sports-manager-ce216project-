import java.util.List;

public interface ITeam {
    String getName();
    String getLogoPath();
    List<IPlayer> getSquad();
    List<IPlayer> getStartingLineup();
    List<ICoach> getCoaches();
    ITactic getCurrentTactic();
    void setTactic(ITactic tactic);
    void setStartingLineup(List<IPlayer> players);
    void substitutePlayer(IPlayer out, IPlayer in);
}
