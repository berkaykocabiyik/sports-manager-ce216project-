import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballTeam Testleri")
class FootballTeamTest {

    private FootballTeam team;
    private FootballCoach coach;

    @BeforeEach
    void setUp() {
        coach = new FootballCoach("C1", "Test Koç", 10, 7.0, "Balanced");
        team = new FootballTeam("T1", "Test Takım", "İstanbul", coach);

        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            team.addPlayer(new FootballPlayer("P" + i, "Oyuncu " + i, 25, pos, 60 + i));
        }
    }

    @Test
    @DisplayName("Takım bilgileri doğru olmalı")
    void testTeamInfo() {
        assertEquals("T1", team.getId());
        assertEquals("Test Takım", team.getName());
        assertEquals("İstanbul", team.getCity());
        assertEquals(coach, team.getCoach());
    }

    @Test
    @DisplayName("Kadro büyüklüğü 18 olmalı")
    void testSquadSize() {
        assertEquals(18, team.getSquadSize());
    }

    @Test
    @DisplayName("18'den fazla oyuncu eklenemez")
    void testMaxSquadSize() {
        assertThrows(RuntimeException.class, () -> {
            team.addPlayer(new FootballPlayer("EXTRA", "Fazla", 20, Position.FORWARD, 70));
        });
    }

    @Test
    @DisplayName("Oyuncu kaldırma")
    void testRemovePlayer() {
        assertEquals(18, team.getSquadSize());
        team.removePlayer("P0");
        assertEquals(17, team.getSquadSize());
    }

    @Test
    @DisplayName("Takım gücü hesaplanmalı")
    void testCalculateTeamStrength() {
        double strength = team.calculateTeamStrength();
        assertTrue(strength > 0);
        assertTrue(strength <= 10.0);
    }

    @Test
    @DisplayName("Sağlıklı oyuncu sayısı")
    void testAvailablePlayerCount() {
        assertEquals(18, team.getAvailablePlayerCount());

        team.getSquad()[0].setInjured(3);
        assertEquals(17, team.getAvailablePlayerCount());
    }

    @Test
    @DisplayName("Lineup kurulumu")
    void testSetupLineup() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();

        team.setupLineup(formation, tactic);

        assertEquals(formation, team.getFormation());
        assertEquals(tactic.getName(), team.getCurrentTactic().getName());
        assertEquals(0, team.getSubstitutionsUsed());
    }

    @Test
    @DisplayName("Yedek oyuncu listesi")
    void testGetSubstitutes() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();
        team.setupLineup(formation, tactic);

        List<Player> subs = team.getSubstitutes();
        assertEquals(7, subs.size()); 
    }

    @Test
    @DisplayName("Oyuncu değişikliği (substitution)")
    void testMakeSubstitution() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();
        team.setupLineup(formation, tactic);

        Player[] lineup = team.getLineup();
        Player outPlayer = lineup[1]; 
        List<Player> subs = team.getSubstitutes();

        assertFalse(subs.isEmpty());
        Player inPlayer = subs.get(0);

        boolean result = team.makeSubstitution(outPlayer, inPlayer);
        assertTrue(result);
        assertEquals(1, team.getSubstitutionsUsed());
    }

    @Test
    @DisplayName("Maksimum 3 oyuncu değişikliği yapılabilir")
    void testMaxSubstitutions() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();
        team.setupLineup(formation, tactic);

        List<Player> subs = team.getSubstitutes();
        Player[] lineup = team.getLineup();

        for (int i = 0; i < 3; i++) {
            Player out = lineup[i + 1]; 
            Player in = subs.get(i);
            assertTrue(team.makeSubstitution(out, in));
        }

        assertEquals(3, team.getSubstitutionsUsed());

        Player extraOut = lineup[5];
        if (extraOut != null) {
            
            FootballPlayer extraIn = new FootballPlayer("EXTRA", "Ekstra", 20, Position.MIDFIELDER, 70);
            assertFalse(team.makeSubstitution(extraOut, extraIn));
        }
    }

    @Test
    @DisplayName("Taktik değişikliği")
    void testChangeTactic() {
        team.setupLineup(
            new FootballFormationFactory().createBalancedFormation(),
            new FootballSport.FootballBalancedTactic()
        );

        assertEquals("Balanced", team.getCurrentTactic().getName());

        team.changeTactic(new FootballSport.FootballOffensiveTactic());
        assertEquals("Offensive", team.getCurrentTactic().getName());
    }

    @Test
    @DisplayName("Formasyon değişikliği")
    void testChangeFormation() {
        team.setupLineup(
            new FootballFormationFactory().createBalancedFormation(),
            new FootballSport.FootballBalancedTactic()
        );

        assertEquals("4-3-3", team.getFormation().getName());

        team.changeFormation(new FootballFormationFactory().createOffensiveFormation());
        assertEquals("3-5-2", team.getFormation().getName());
    }

    @Test
    @DisplayName("Değişiklik sayacı sıfırlanmalı")
    void testResetSubstitutions() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();
        team.setupLineup(formation, tactic);

        List<Player> subs = team.getSubstitutes();
        Player[] lineup = team.getLineup();

        team.makeSubstitution(lineup[1], subs.get(0));
        assertEquals(1, team.getSubstitutionsUsed());

        team.resetSubstitutions();
        assertEquals(0, team.getSubstitutionsUsed());
    }

    @Test
    @DisplayName("Sakatlık süreleri azaltılmalı")
    void testDecrementAllInjuries() {
        team.getSquad()[0].setInjured(3);
        team.getSquad()[1].setInjured(1);

        team.decrementAllInjuries();

        assertEquals(2, team.getSquad()[0].getInjuredMatchesRemaining());
        assertEquals(0, team.getSquad()[1].getInjuredMatchesRemaining());
        assertFalse(team.getSquad()[1].isInjured());
    }

    @Test
    @DisplayName("Maç sonucu kaydı")
    void testRecordMatchResult() {
        team.recordMatchResult(3, 1, true, false);
        assertEquals(1, team.getTotalWins());
        assertEquals(0, team.getTotalDraws());
        assertEquals(0, team.getTotalLosses());
        assertEquals(3, team.getGoalsFor());
        assertEquals(1, team.getGoalsAgainst());
        assertEquals(2, team.getGoalDifference());

        team.recordMatchResult(1, 1, false, true);
        assertEquals(1, team.getTotalWins());
        assertEquals(1, team.getTotalDraws());
        assertEquals(4, team.getGoalsFor());
    }

    @Test
    @DisplayName("TeamStats doğru dönmeli")
    void testGetStats() {
        team.recordMatchResult(2, 0, true, false);
        team.recordMatchResult(1, 1, false, true);
        team.recordMatchResult(0, 3, false, false);

        TeamStats stats = team.getStats();
        assertNotNull(stats);
        assertEquals("Test Takım", stats.getTeamName());
        assertEquals(3, stats.getMatchesPlayed());
        assertEquals(4, stats.getPoints()); 
        assertEquals(3, stats.getGoalsFor());
        assertEquals(4, stats.getGoalsAgainst());
        assertEquals(-1, stats.getGoalDifference());
        assertNotNull(stats.getSummary());
    }

    @Test
    @DisplayName("Koç değiştirme")
    void testSetCoach() {
        FootballCoach newCoach = new FootballCoach("C2", "Yeni Koç", 20, 9.0, "Offensive");
        team.setCoach(newCoach);
        assertEquals("Yeni Koç", team.getCoach().getName());
    }
}
