import java.util.List;

public abstract class AbstractTeam implements ITeam {
    protected String name;
    protected String logoPath;
    protected List<IPlayer> squad;
    protected List<IPlayer> startingLineup;
    protected ITactic currentTactic;

    @Override public void substitutePlayer(IPlayer out, IPlayer in) {
        int idx = startingLineup.indexOf(out);
        if (idx >= 0) startingLineup.set(idx, in);
    }

    @Override public abstract void setTactic(ITactic tactic);
}
