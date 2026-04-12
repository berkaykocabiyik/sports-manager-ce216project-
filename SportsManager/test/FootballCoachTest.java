import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballCoach Testleri")
class FootballCoachTest {

    private FootballCoach coach;
    private FootballTeam strongTeam;
    private FootballTeam weakTeam;
    private FootballTeam midTeam;

    @BeforeEach
    void setUp() {
        coach = new FootballCoach("C1", "Test Koç", 15, 8.0, "Balanced");

        FootballCoach c1 = new FootballCoach("CS", "Güçlü Koç", 20, 9.0, "Offensive");
        strongTeam = new FootballTeam("TS", "Güçlü Takım", "İstanbul", c1);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            strongTeam.addPlayer(new FootballPlayer("SP" + i, "Güçlü " + i, 25, pos, 90));
        }

        FootballCoach c2 = new FootballCoach("CW", "Zayıf Koç", 1, 2.0, "Defensive");
        weakTeam = new FootballTeam("TW", "Zayıf Takım", "Ankara", c2);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            weakTeam.addPlayer(new FootballPlayer("WP" + i, "Zayıf " + i, 25, pos, 40));
        }

        FootballCoach c3 = new FootballCoach("CM", "Orta Koç", 5, 5.0, "Balanced");
        midTeam = new FootballTeam("TM", "Orta Takım", "İzmir", c3);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            midTeam.addPlayer(new FootballPlayer("MP" + i, "Orta " + i, 25, pos, 55));
        }
    }

    @Test
    @DisplayName("Koç bilgileri doğru olmalı")
    void testCoachInfo() {
        assertEquals("C1", coach.getId());
        assertEquals("Test Koç", coach.getName());
        assertEquals(15, coach.getExperience());
        assertEquals(8.0, coach.getCoachingRating(), 0.01);
        assertEquals("Balanced", coach.getSpecialty());
    }

    @Test
    @DisplayName("Başarı rating'i hesaplanmalı")
    void testSuccessRating() {
        double successRating = coach.getSuccessRating();
        assertTrue(successRating > 0);
        
        assertEquals(4.75, successRating, 0.01);
    }

    @Test
    @DisplayName("Güçlü rakibe karşı savunmacı taktik seçilmeli")
    void testDefensiveTacticSelection() {
        Tactic tactic = coach.selectTactic(weakTeam, strongTeam);
        assertEquals("Defensive", tactic.getName());
    }

    @Test
    @DisplayName("Zayıf rakibe karşı hücum taktiği seçilmeli")
    void testOffensiveTacticSelection() {
        Tactic tactic = coach.selectTactic(strongTeam, weakTeam);
        assertEquals("Offensive", tactic.getName());
    }

    @Test
    @DisplayName("Eşit güçte dengeli taktik seçilmeli")
    void testBalancedTacticSelection() {
        Tactic tactic = coach.selectTactic(midTeam, midTeam);
        assertEquals("Balanced", tactic.getName());
    }

    @Test
    @DisplayName("Güçlü takım için hücum formasyonu seçilmeli (strength > 8)")
    void testFormationForStrongTeam() {
        Formation formation = coach.selectFormation(strongTeam);
        assertEquals("3-5-2", formation.getName()); 
    }

    @Test
    @DisplayName("Zayıf takım için savunma formasyonu seçilmeli (strength < 5)")
    void testFormationForWeakTeam() {
        Formation formation = coach.selectFormation(weakTeam);
        assertEquals("4-4-2", formation.getName()); 
    }

    @Test
    @DisplayName("Orta takım için dengeli formasyon seçilmeli (strength 5-8 arası)")
    void testFormationForMidTeam() {
        Formation formation = coach.selectFormation(midTeam);
        assertEquals("4-3-3", formation.getName()); 
    }

    @Test
    @DisplayName("Devre arası - geriden gelirken hücum taktiği")
    void testHalftimeOffensiveTactic() {
        Tactic tactic = coach.selectHalftimeTactic(0, 2, true); 
        assertNotNull(tactic);
        assertEquals("Offensive", tactic.getName());
    }

    @Test
    @DisplayName("Devre arası - öndeyken savunma taktiği")
    void testHalftimeDefensiveTactic() {
        Tactic tactic = coach.selectHalftimeTactic(3, 1, true); 
        assertNotNull(tactic);
        assertEquals("Defensive", tactic.getName());
    }

    @Test
    @DisplayName("Devre arası - beraberlikte taktik değişikliği yapılmamalı")
    void testHalftimeNoChangeOnDraw() {
        Tactic tactic = coach.selectHalftimeTactic(1, 1, true);
        assertNull(tactic);
    }

    @Test
    @DisplayName("Devre arası - geriden gelirken hücum formasyonu")
    void testHalftimeOffensiveFormation() {
        Formation formation = coach.selectHalftimeFormation(midTeam, 0, 1, true);
        assertNotNull(formation);
        assertEquals("3-5-2", formation.getName());
    }

    @Test
    @DisplayName("Devre arası - öndeyken savunma formasyonu")
    void testHalftimeDefensiveFormation() {
        Formation formation = coach.selectHalftimeFormation(midTeam, 2, 0, true);
        assertNotNull(formation);
        assertEquals("4-4-2", formation.getName());
    }

    @Test
    @DisplayName("Lineup kurulumu çalışmalı")
    void testSetupLineup() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();

        coach.setupLineup(midTeam, formation, tactic);

        Player[] lineup = midTeam.getLineup();
        int filledSlots = 0;
        for (Player p : lineup) {
            if (p != null) filledSlots++;
        }
        assertEquals(11, filledSlots);
    }

    @Test
    @DisplayName("Lineup'ta en iyi oyuncular seçilmeli")
    void testBestPlayersInLineup() {
        Formation formation = new FootballFormationFactory().createBalancedFormation();
        Tactic tactic = new FootballSport.FootballBalancedTactic();

        coach.setupLineup(strongTeam, formation, tactic);

        Player[] lineup = strongTeam.getLineup();
        for (Player p : lineup) {
            if (p != null) {
                assertTrue(p.canPlay());
            }
        }
    }
}
