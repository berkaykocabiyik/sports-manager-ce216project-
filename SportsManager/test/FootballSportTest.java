import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballSport Testleri")
class FootballSportTest {

    private FootballSport sport;

    @BeforeEach
    void setUp() {
        sport = new FootballSport();
    }

    @Test
    @DisplayName("Spor ismi Futbol olmalı")
    void testGetName() {
        assertEquals("Futbol", sport.getName());
    }

    @Test
    @DisplayName("Sahada 11 oyuncu olmalı")
    void testPlayerCount() {
        assertEquals(11, sport.getPlayerCount());
    }

    @Test
    @DisplayName("7 yedek oyuncu olmalı")
    void testSubstituteCount() {
        assertEquals(7, sport.getSubstituteCount());
    }

    @Test
    @DisplayName("Kadro büyüklüğü 18 olmalı (11+7)")
    void testSquadSize() {
        assertEquals(18, FootballSport.SQUAD_SIZE);
    }

    @Test
    @DisplayName("Lig 18 takımdan oluşmalı")
    void testTeamCount() {
        assertEquals(18, FootballSport.TEAM_COUNT);
    }

    @Test
    @DisplayName("Maç 2 yarıdan oluşmalı")
    void testMatchPeriods() {
        assertEquals(2, sport.getMatchPeriods());
    }

    @Test
    @DisplayName("Her yarı 45 dakika olmalı")
    void testPeriodDuration() {
        assertEquals(45, sport.getPeriodDuration());
    }

    @Test
    @DisplayName("Galibiyet 3 puan olmalı")
    void testWinPoints() {
        assertEquals(3, sport.getWinPoints());
    }

    @Test
    @DisplayName("Beraberlik 1 puan olmalı")
    void testDrawPoints() {
        assertEquals(1, sport.getDrawPoints());
    }

    @Test
    @DisplayName("Mağlubiyet 0 puan olmalı")
    void testLossPoints() {
        assertEquals(0, sport.getLossPoints());
    }

    @Test
    @DisplayName("Geçerli pozisyonlar: GK, DEF, MID, FWD")
    void testValidPositions() {
        Position[] positions = sport.getValidPositions();
        assertEquals(4, positions.length);
        assertEquals(Position.GOALKEEPER, positions[0]);
        assertEquals(Position.DEFENDER, positions[1]);
        assertEquals(Position.MIDFIELDER, positions[2]);
        assertEquals(Position.FORWARD, positions[3]);
    }

    @Test
    @DisplayName("3 taktik mevcut olmalı: Defensive, Balanced, Offensive")
    void testValidTactics() {
        Tactic[] tactics = sport.getValidTactics();
        assertEquals(3, tactics.length);
        assertEquals("Defensive", tactics[0].getName());
        assertEquals("Balanced", tactics[1].getName());
        assertEquals("Offensive", tactics[2].getName());
    }

    @Test
    @DisplayName("Taktik agresiflik seviyeleri doğru olmalı")
    void testTacticAggressiveness() {
        Tactic[] tactics = sport.getValidTactics();
        assertEquals(3, tactics[0].getAggressivenessLevel()); 
        assertEquals(5, tactics[1].getAggressivenessLevel()); 
        assertEquals(9, tactics[2].getAggressivenessLevel()); 
    }

    @Test
    @DisplayName("Taktik savunma güçleri doğru olmalı")
    void testTacticDefenseStrength() {
        Tactic[] tactics = sport.getValidTactics();
        assertEquals(9, tactics[0].getDefenseStrength()); 
        assertEquals(5, tactics[1].getDefenseStrength()); 
        assertEquals(2, tactics[2].getDefenseStrength()); 
    }

    @Test
    @DisplayName("FormationFactory null olmamalı")
    void testFormationFactory() {
        assertNotNull(sport.getFormationFactory());
        assertTrue(sport.getFormationFactory() instanceof FootballFormationFactory);
    }

    @Test
    @DisplayName("Geçerli maç sonucu kontrolü")
    void testValidMatchResult() {
        assertTrue(sport.isValidMatchResult(0, 0));
        assertTrue(sport.isValidMatchResult(3, 1));
        assertTrue(sport.isValidMatchResult(0, 5));
        assertFalse(sport.isValidMatchResult(-1, 0));
        assertFalse(sport.isValidMatchResult(0, -1));
    }

    @Test
    @DisplayName("Sıralama kriteri tanımlanmış olmalı")
    void testTiebreakerRule() {
        String rule = sport.getTiebreakerRule();
        assertNotNull(rule);
        assertTrue(rule.contains("Puan"));
        assertTrue(rule.contains("Averaj"));
        assertTrue(rule.contains("Gol Farkı"));
        assertTrue(rule.contains("Kura"));
    }

    @Test
    @DisplayName("Sakatlık kuralı tanımlanmış olmalı")
    void testInjuryRule() {
        String rule = sport.getInjuryRule();
        assertNotNull(rule);
        assertTrue(rule.contains("0-2"));
        assertTrue(rule.contains("1-5"));
    }

    @Test
    @DisplayName("Minimum skor 0 olmalı")
    void testMinimumScore() {
        assertEquals(0, sport.getMinimumScore());
    }
}
