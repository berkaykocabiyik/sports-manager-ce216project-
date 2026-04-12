import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballPlayer Testleri")
class FootballPlayerTest {

    private FootballPlayer player;

    @BeforeEach
    void setUp() {
        player = new FootballPlayer("P1", "Test Oyuncu", 25, Position.FORWARD, 80);
    }

    @Test
    @DisplayName("OVR rating 40-99 arasında olmalı")
    void testOVRRatingBounds() {
        assertEquals(80, player.getOverallRating());

        FootballPlayer lowPlayer = new FootballPlayer("P2", "Low", 20, Position.DEFENDER, 30);
        assertEquals(40, lowPlayer.getOverallRating()); 

        FootballPlayer highPlayer = new FootballPlayer("P3", "High", 20, Position.DEFENDER, 110);
        assertEquals(99, highPlayer.getOverallRating()); 
    }

    @Test
    @DisplayName("OVR rating'den base rating doğru hesaplanmalı")
    void testBaseRatingFromOVR() {
        assertEquals(8.0, player.getRating(), 0.01); 

        FootballPlayer p2 = new FootballPlayer("P2", "Test", 20, Position.MIDFIELDER, 65);
        assertEquals(6.5, p2.getRating(), 0.01);
    }

    @Test
    @DisplayName("Gol ekleme")
    void testAddGoal() {
        assertEquals(0, player.getGoals());
        player.addGoal();
        assertEquals(1, player.getGoals());
        player.addGoal();
        player.addGoal();
        assertEquals(3, player.getGoals());
    }

    @Test
    @DisplayName("Asist ekleme")
    void testAddAssist() {
        assertEquals(0, player.getAssists());
        player.addAssist();
        assertEquals(1, player.getAssists());
    }

    @Test
    @DisplayName("Tackle ekleme")
    void testAddTackle() {
        assertEquals(0, player.getTackles());
        player.addTackle();
        player.addTackle();
        assertEquals(2, player.getTackles());
    }

    @Test
    @DisplayName("Pas ekleme ve doğruluk hesaplama")
    void testPassAccuracy() {
        assertEquals(0, player.getPasses());
        assertEquals(0, player.getPassAccuracy());

        player.addPass(true);
        player.addPass(true);
        player.addPass(false);
        assertEquals(3, player.getPasses());
        assertEquals(2, player.getAccuratePasses());
        assertEquals(66, player.getPassAccuracy()); 
    }

    @Test
    @DisplayName("Performans hesaplama")
    void testCalculatePerformance() {
        double basePerformance = player.calculatePerformance();
        assertTrue(basePerformance > 0);

        player.addGoal();
        player.addAssist();
        double afterGoalAndAssist = player.calculatePerformance();
        assertTrue(afterGoalAndAssist > basePerformance);
    }

    @Test
    @DisplayName("Performans 10.0'dan büyük olmamalı")
    void testPerformanceCap() {
        for (int i = 0; i < 50; i++) {
            player.addGoal();
            player.addAssist();
            player.addTackle();
        }
        assertTrue(player.calculatePerformance() <= 10.0);
    }

    @Test
    @DisplayName("PlayerStats doğru dönmeli")
    void testGetStats() {
        player.addGoal();
        player.addGoal();
        player.addAssist();

        PlayerStats stats = player.getStats();
        assertNotNull(stats);
        assertEquals("P1", stats.getPlayerId());
        assertEquals("Test Oyuncu", stats.getPlayerName());
        assertTrue(stats.getPerformanceRating() > 0);
        assertNotNull(stats.getSummary());
        assertTrue(stats.getSummary().contains("Test Oyuncu"));
    }

    @Test
    @DisplayName("Sakatlık sistemi")
    void testInjurySystem() {
        assertFalse(player.isInjured());
        assertTrue(player.canPlay());

        player.setInjured(3);
        assertTrue(player.isInjured());
        assertFalse(player.canPlay());
        assertEquals(3, player.getInjuredMatchesRemaining());

        player.decrementInjury();
        assertEquals(2, player.getInjuredMatchesRemaining());
        assertTrue(player.isInjured());

        player.decrementInjury();
        player.decrementInjury();
        assertEquals(0, player.getInjuredMatchesRemaining());
        assertFalse(player.isInjured());
        assertTrue(player.canPlay());
    }

    @Test
    @DisplayName("Sarı ve kırmızı kart sistemi")
    void testCardSystem() {
        assertEquals(0, player.getYellowCards());
        assertEquals(0, player.getRedCards());

        player.addYellowCard();
        assertEquals(1, player.getYellowCards());
        assertTrue(player.canPlay());

        player.addRedCard();
        assertEquals(1, player.getRedCards());
        assertFalse(player.canPlay());
    }

    @Test
    @DisplayName("OVR güncelleme")
    void testSetOverallRating() {
        player.setOverallRating(90);
        assertEquals(90, player.getOverallRating());
        assertEquals(9.0, player.getRating(), 0.01);

        player.setOverallRating(20); 
        assertEquals(40, player.getOverallRating());
    }

    @Test
    @DisplayName("Pozisyon kontrolü")
    void testPositions() {
        assertEquals(Position.FORWARD, player.getPosition());

        FootballPlayer gk = new FootballPlayer("GK1", "Kaleci", 28, Position.GOALKEEPER, 75);
        assertEquals(Position.GOALKEEPER, gk.getPosition());

        FootballPlayer def = new FootballPlayer("DEF1", "Defans", 26, Position.DEFENDER, 70);
        assertEquals(Position.DEFENDER, def.getPosition());

        FootballPlayer mid = new FootballPlayer("MID1", "Ortasaha", 24, Position.MIDFIELDER, 72);
        assertEquals(Position.MIDFIELDER, mid.getPosition());
    }

    @Test
    @DisplayName("Eski constructor ile uyumluluk")
    void testLegacyConstructor() {
        FootballPlayer legacy = new FootballPlayer("L1", "Eski", 22, Position.MIDFIELDER, 7.5);
        assertEquals(75, legacy.getOverallRating());
        assertEquals(7.5, legacy.getRating(), 0.01);
    }
}
