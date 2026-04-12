import game.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NameGenerator Testleri")
class NameGeneratorTest {

    @Test
    @DisplayName("Oyuncu ismi üretilmeli")
    void testGeneratePlayerName() {
        String name = NameGenerator.generatePlayerName();
        assertNotNull(name);
        assertFalse(name.isEmpty());
        assertTrue(name.contains(" ")); 
    }

    @Test
    @DisplayName("Koç ismi üretilmeli")
    void testGenerateCoachName() {
        String name = NameGenerator.generateCoachName();
        assertNotNull(name);
        assertFalse(name.isEmpty());
        assertTrue(name.contains(" "));
    }

    @RepeatedTest(10)
    @DisplayName("Farklı isimler üretilmeli (rastgelelik)")
    void testNameRandomness() {
        Set<String> names = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            names.add(NameGenerator.generatePlayerName());
        }
        
        assertTrue(names.size() >= 10, "Yeterli çeşitlilik yok: " + names.size());
    }

    @Test
    @DisplayName("Takım isimleri index bazlı dönmeli")
    void testGetTeamName() {
        String name0 = NameGenerator.getTeamName(0);
        String name1 = NameGenerator.getTeamName(1);
        assertNotNull(name0);
        assertNotNull(name1);
        assertNotEquals(name0, name1);
    }

    @Test
    @DisplayName("Index dışı takım ismi")
    void testGetTeamNameOutOfRange() {
        String name = NameGenerator.getTeamName(100);
        assertNotNull(name);
        assertTrue(name.startsWith("Takım")); 
    }

    @Test
    @DisplayName("Şehir isimleri index bazlı dönmeli")
    void testGetCityName() {
        String city0 = NameGenerator.getCityName(0);
        String city1 = NameGenerator.getCityName(1);
        assertNotNull(city0);
        assertNotNull(city1);
    }

    @Test
    @DisplayName("Koç uzmanlığı boş olmamalı")
    void testGetRandomSpecialty() {
        String specialty = NameGenerator.getRandomSpecialty();
        assertNotNull(specialty);
        assertFalse(specialty.isEmpty());
    }

    @Test
    @DisplayName("OVR rating 40-99 arası olmalı")
    void testGenerateOVR() {
        for (int i = 0; i < 100; i++) {
            int ovr = NameGenerator.generateOVR();
            assertTrue(ovr >= 40 && ovr <= 99,
                "OVR sınır dışı: " + ovr);
        }
    }

    @Test
    @DisplayName("Oyuncu yaşı 17-38 arası olmalı")
    void testGeneratePlayerAge() {
        for (int i = 0; i < 100; i++) {
            int age = NameGenerator.generatePlayerAge();
            assertTrue(age >= 17 && age <= 38,
                "Yaş sınır dışı: " + age);
        }
    }

    @Test
    @DisplayName("Koç deneyimi 1-30 arası olmalı")
    void testGenerateCoachExperience() {
        for (int i = 0; i < 100; i++) {
            int exp = NameGenerator.generateCoachExperience();
            assertTrue(exp >= 1 && exp <= 30,
                "Deneyim sınır dışı: " + exp);
        }
    }

    @Test
    @DisplayName("Koç rating'i 40-99 arası olmalı")
    void testGenerateCoachRating() {
        for (int i = 0; i < 100; i++) {
            int rating = NameGenerator.generateCoachRating();
            assertTrue(rating >= 40 && rating <= 99,
                "Rating sınır dışı: " + rating);
        }
    }

    @Test
    @DisplayName("Pozisyon dağılımı doğru olmalı (2GK + 6DEF + 6MID + 4FWD)")
    void testGetPositionForIndex() {
        
        assertEquals(Position.GOALKEEPER, NameGenerator.getPositionForIndex(0));
        assertEquals(Position.GOALKEEPER, NameGenerator.getPositionForIndex(1));

        assertEquals(Position.DEFENDER, NameGenerator.getPositionForIndex(2));
        assertEquals(Position.DEFENDER, NameGenerator.getPositionForIndex(7));

        assertEquals(Position.MIDFIELDER, NameGenerator.getPositionForIndex(8));
        assertEquals(Position.MIDFIELDER, NameGenerator.getPositionForIndex(13));

        assertEquals(Position.FORWARD, NameGenerator.getPositionForIndex(14));
        assertEquals(Position.FORWARD, NameGenerator.getPositionForIndex(17));
    }

    @Test
    @DisplayName("18 oyuncunun pozisyon dağılımı")
    void testPositionDistribution() {
        int gk = 0, def = 0, mid = 0, fwd = 0;
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            switch (pos) {
                case GOALKEEPER -> gk++;
                case DEFENDER -> def++;
                case MIDFIELDER -> mid++;
                case FORWARD -> fwd++;
                default -> fail("Beklenmeyen pozisyon: " + pos);
            }
        }

        assertEquals(2, gk, "2 kaleci olmalı");
        assertEquals(6, def, "6 defans olmalı");
        assertEquals(6, mid, "6 orta saha olmalı");
        assertEquals(4, fwd, "4 forvet olmalı");
        assertEquals(18, gk + def + mid + fwd, "Toplam 18 oyuncu olmalı");
    }
}
