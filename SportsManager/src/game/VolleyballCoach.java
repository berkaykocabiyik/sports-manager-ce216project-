package game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VolleyballCoach extends Coach {

    public VolleyballCoach(String id, String name, int experience, double coachingRating, String specialty) {
        super(id, name, experience, coachingRating, specialty);
    }

    @Override
    public Tactic selectTactic(Team team, Team opponent) {
        double teamStrength = team.calculateTeamStrength();
        double opponentStrength = opponent.calculateTeamStrength();

        if (opponentStrength > teamStrength + 0.8) {
            return new VolleyballSport.BlockDefenseTactic();
        }
        if (teamStrength > opponentStrength + 0.8) {
            return new VolleyballSport.ServePressureTactic();
        }
        return new VolleyballSport.BalancedVolleyTactic();
    }

    @Override
    public Formation selectFormation(Team team) {
        VolleyballFormationFactory factory = new VolleyballFormationFactory();
        double strength = team.calculateTeamStrength();
        if (strength >= 8.0) {
            return factory.createOffensiveFormation();
        }
        if (strength <= 5.5) {
            return factory.createDefensiveFormation();
        }
        return factory.createBalancedFormation();
    }

    @Override
    public void setupLineup(Team team, Formation formation, Tactic tactic) {
        if (!(team instanceof VolleyballTeam volleyballTeam)) {
            return;
        }

        Player[] lineup = new Player[VolleyballSport.PLAYER_COUNT];
        int index = 0;

        List<Player> setters = playersFor(volleyballTeam, Position.SETTER);
        List<Player> liberos = playersFor(volleyballTeam, Position.LIBERO);
        List<Player> spikers = playersFor(volleyballTeam, Position.SPIKER);

        index = fill(lineup, index, setters, formation.getPositionCount(Position.SETTER));
        index = fill(lineup, index, liberos, formation.getPositionCount(Position.LIBERO));
        index = fill(lineup, index, spikers, formation.getPositionCount(Position.SPIKER));

        if (index < lineup.length) {
            List<Player> remaining = new ArrayList<>();
            for (Player player : volleyballTeam.getSquad()) {
                if (player != null && player.canPlay() && !contains(lineup, player)) {
                    remaining.add(player);
                }
            }
            remaining.sort(Comparator.comparingDouble(Player::getRating).reversed());
            index = fill(lineup, index, remaining, lineup.length - index);
        }

        Player[] target = volleyballTeam.getLineup();
        for (int i = 0; i < target.length; i++) {
            target[i] = i < lineup.length ? lineup[i] : null;
        }
    }

    private List<Player> playersFor(VolleyballTeam team, Position position) {
        List<Player> players = new ArrayList<>();
        for (Player player : team.getSquad()) {
            if (player != null && player.canPlay() && player.getPosition() == position) {
                players.add(player);
            }
        }
        players.sort(Comparator.comparingDouble(Player::getRating).reversed());
        return players;
    }

    private int fill(Player[] lineup, int index, List<Player> candidates, int needed) {
        int added = 0;
        for (Player candidate : candidates) {
            if (index >= lineup.length || added >= needed) {
                break;
            }
            if (!contains(lineup, candidate)) {
                lineup[index++] = candidate;
                added++;
            }
        }
        return index;
    }

    private boolean contains(Player[] lineup, Player player) {
        for (Player lineupPlayer : lineup) {
            if (lineupPlayer != null && lineupPlayer.getId().equals(player.getId())) {
                return true;
            }
        }
        return false;
    }
}
