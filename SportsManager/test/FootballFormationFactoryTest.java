import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballFormationFactory Testleri")
class FootballFormationFactoryTest {

    private FootballFormationFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FootballFormationFactory();
    }

    @Test
    @DisplayName("4-4-2 formasyonu doğru pozisyon sayılarına sahip olmalı")
    void testFormation442() {
        Formation f = factory.createDefensiveFormation();
        assertEquals("4-4-2", f.getName());
        assertEquals(1, f.getPositionCount(Position.GOALKEEPER));
        assertEquals(4, f.getPositionCount(Position.DEFENDER));
        assertEquals(4, f.getPositionCount(Position.MIDFIELDER));
        assertEquals(2, f.getPositionCount(Position.FORWARD));
        assertEquals(11, f.getTotalPlayerCount());
    }

    @Test
    @DisplayName("4-3-3 formasyonu doğru pozisyon sayılarına sahip olmalı")
    void testFormation433() {
        Formation f = factory.createBalancedFormation();
        assertEquals("4-3-3", f.getName());
        assertEquals(1, f.getPositionCount(Position.GOALKEEPER));
        assertEquals(4, f.getPositionCount(Position.DEFENDER));
        assertEquals(3, f.getPositionCount(Position.MIDFIELDER));
        assertEquals(3, f.getPositionCount(Position.FORWARD));
        assertEquals(11, f.getTotalPlayerCount());
    }

    @Test
    @DisplayName("3-5-2 formasyonu doğru pozisyon sayılarına sahip olmalı")
    void testFormation352() {
        Formation f = factory.createOffensiveFormation();
        assertEquals("3-5-2", f.getName());
        assertEquals(1, f.getPositionCount(Position.GOALKEEPER));
        assertEquals(3, f.getPositionCount(Position.DEFENDER));
        assertEquals(5, f.getPositionCount(Position.MIDFIELDER));
        assertEquals(2, f.getPositionCount(Position.FORWARD));
        assertEquals(11, f.getTotalPlayerCount());
    }

    @Test
    @DisplayName("Tüm formasyonlar 11 oyuncudan oluşmalı")
    void testAllFormationsHave11Players() {
        Formation[] all = factory.getAllFormations();
        for (Formation f : all) {
            assertEquals(11, f.getTotalPlayerCount(),
                f.getName() + " formasyonu 11 oyuncu olmalı");
        }
    }

    @Test
    @DisplayName("3 farklı formasyon mevcut olmalı (4-4-2, 4-3-3, 3-5-2)")
    void testAllFormationsCount() {
        Formation[] all = factory.getAllFormations();
        assertEquals(3, all.length);
    }

    @Test
    @DisplayName("İsimle formasyon oluşturma")
    void testCreateFormationByName() {
        Formation f442 = factory.createFormation("4-4-2");
        assertEquals("4-4-2", f442.getName());

        Formation f433 = factory.createFormation("4-3-3");
        assertEquals("4-3-3", f433.getName());

        Formation f352 = factory.createFormation("3-5-2");
        assertEquals("3-5-2", f352.getName());
    }

    @Test
    @DisplayName("Bilinmeyen formasyon adı dengeli formasyonu döndürmeli")
    void testUnknownFormationReturnsBalanced() {
        Formation f = factory.createFormation("unknown");
        assertEquals("4-3-3", f.getName()); 
    }

    @Test
    @DisplayName("Formasyonların hücum ve savunma güçleri tutarlı olmalı")
    void testFormationStrengths() {
        Formation defensive = factory.createDefensiveFormation();
        Formation balanced = factory.createBalancedFormation();
        Formation offensive = factory.createOffensiveFormation();

        assertTrue(defensive.getDefensiveStrength() > offensive.getDefensiveStrength());

        assertTrue(offensive.getOffensiveStrength() > defensive.getOffensiveStrength());
    }

    @Test
    @DisplayName("Formasyon gerekli pozisyonları içermeli")
    void testRequiredPositions() {
        Formation f = factory.createBalancedFormation();
        Position[] required = f.getRequiredPositions();

        assertEquals(4, required.length);
        assertEquals(Position.GOALKEEPER, required[0]);
        assertEquals(Position.DEFENDER, required[1]);
        assertEquals(Position.MIDFIELDER, required[2]);
        assertEquals(Position.FORWARD, required[3]);
    }

    @Test
    @DisplayName("Formasyon açıklaması boş olmamalı")
    void testFormationDescription() {
        Formation[] all = factory.getAllFormations();
        for (Formation f : all) {
            assertNotNull(f.getDescription());
            assertFalse(f.getDescription().isEmpty());
        }
    }

    @Test
    @DisplayName("Geçersiz pozisyon 0 döndürmeli")
    void testInvalidPositionCount() {
        Formation f = factory.createBalancedFormation();
        assertEquals(0, f.getPositionCount(Position.POINT_GUARD));
        assertEquals(0, f.getPositionCount(Position.SETTER));
    }

    @Test
    @DisplayName("Her formasyonda tam 1 kaleci olmalı")
    void testOneGoalkeeperPerFormation() {
        Formation[] all = factory.getAllFormations();
        for (Formation f : all) {
            assertEquals(1, f.getPositionCount(Position.GOALKEEPER),
                f.getName() + " formasyonunda 1 kaleci olmalı");
        }
    }
}
