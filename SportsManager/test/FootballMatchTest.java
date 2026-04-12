import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballMatch Testleri")
class FootballMatchTest {

    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private FootballMatch match;

    @BeforeEach
    void setUp() {
        FootballCoach homeCoach = new FootballCoach("CH", "Ev Koç", 10, 7.5, "Balanced");
        homeTeam = new FootballTeam("TH", "Ev Takım", "İstanbul", homeCoach);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            homeTeam.addPlayer(new FootballPlayer("HP" + i, "EvOyuncu " + i, 25, pos, 70));
        }

        FootballCoach awayCoach = new FootballCoach("CA", "Dep Koç", 10, 7.0, "Defensive");
        awayTeam = new FootballTeam("TA", "Dep Takım", "Ankara", awayCoach);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            awayTeam.addPlayer(new FootballPlayer("AP" + i, "DepOyuncu " + i, 25, pos, 68));
        }

        Formation formation = new FootballFormationFactory().createBalancedFormation();
        homeTeam.setupLineup(formation, new FootballSport.FootballBalancedTactic());
        awayTeam.setupLineup(formation, new FootballSport.FootballBalancedTactic());

        match = new FootballMatch("M1", homeTeam, awayTeam, LocalDateTime.now());
    }

    @Test
    @DisplayName("Maç başlangıç durumu doğru olmalı")
    void testInitialState() {
        assertEquals("M1", match.getId());
        assertEquals(homeTeam, match.getHomeTeam());
        assertEquals(awayTeam, match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        assertEquals(MatchStatus.SCHEDULED, match.getStatus());
    }

    @Test
    @DisplayName("Maç simülasyonu çalışmalı")
    void testSimulateMatch() {
        match.simulateMatch();
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertTrue(match.getHomeScore() >= 0);
        assertTrue(match.getAwayScore() >= 0);
    }

    @Test
    @DisplayName("Maç sonucu doğru dönmeli")
    void testMatchResult() {
        match.simulateMatch();
        MatchResult result = match.getMatchResult();

        assertNotNull(result);
        assertEquals(homeTeam, result.getHomeTeam());
        assertEquals(awayTeam, result.getAwayTeam());
        assertEquals(match.getHomeScore(), result.getHomeScore());
        assertEquals(match.getAwayScore(), result.getAwayScore());

        if (result.getHomeScore() > result.getAwayScore()) {
            assertEquals(MatchOutcome.HOME_WIN, result.getOutcome());
            assertEquals(homeTeam, result.getWinner());
        } else if (result.getHomeScore() < result.getAwayScore()) {
            assertEquals(MatchOutcome.AWAY_WIN, result.getOutcome());
            assertEquals(awayTeam, result.getWinner());
        } else {
            assertEquals(MatchOutcome.DRAW, result.getOutcome());
            assertNull(result.getWinner());
        }
    }

    @Test
    @DisplayName("Maç sonucu özeti boş olmamalı")
    void testMatchResultSummary() {
        match.simulateMatch();
        MatchResult result = match.getMatchResult();
        assertNotNull(result.getSummary());
        assertFalse(result.getSummary().isEmpty());
        assertTrue(result.getSummary().contains("Ev Takım"));
        assertTrue(result.getSummary().contains("Dep Takım"));
    }

    @Test
    @DisplayName("Maç olayları kaydedilmeli")
    void testMatchEvents() {
        match.simulateMatch();
        List<String> events = match.getMatchEvents();
        assertNotNull(events);
        
    }

    @Test
    @DisplayName("Observer pattern çalışmalı")
    void testObserverPattern() {
        final int[] updateCount = {0};
        MatchObserver observer = (m, event) -> updateCount[0]++;

        match.addObserver(observer);
        match.simulateMatch();

        assertTrue(updateCount[0] > 0, "Observer en az bir kez güncellenmiş olmalı");
    }

    @Test
    @DisplayName("Observer kaldırma")
    void testRemoveObserver() {
        final int[] updateCount = {0};
        MatchObserver observer = (m, event) -> updateCount[0]++;

        match.addObserver(observer);
        match.removeObserver(observer);
        match.simulateMatch();

        assertEquals(0, updateCount[0], "Kaldırılan observer güncellenmemeli");
    }

    @Test
    @DisplayName("Maç sonrası takım sonuçları kaydedilmeli")
    void testRecordResults() {
        match.simulateMatch();

        int homeWins = homeTeam.getTotalWins();
        int homeDraws = homeTeam.getTotalDraws();
        int homeLosses = homeTeam.getTotalLosses();

        int awayWins = awayTeam.getTotalWins();
        int awayDraws = awayTeam.getTotalDraws();
        int awayLosses = awayTeam.getTotalLosses();

        assertEquals(1, homeWins + homeDraws + homeLosses);
        assertEquals(1, awayWins + awayDraws + awayLosses);

        if (match.getHomeScore() > match.getAwayScore()) {
            assertEquals(1, homeWins);
            assertEquals(1, awayLosses);
        } else if (match.getHomeScore() < match.getAwayScore()) {
            assertEquals(1, homeLosses);
            assertEquals(1, awayWins);
        } else {
            assertEquals(1, homeDraws);
            assertEquals(1, awayDraws);
        }
    }

    @RepeatedTest(5)
    @DisplayName("Maç sonrası sakatlıklar 0-2 arası olmalı")
    void testPostMatchInjuries() {
        
        FootballCoach hc = new FootballCoach("CH2", "Koç H", 10, 7.0, "Balanced");
        FootballTeam ht = new FootballTeam("TH2", "EvT", "İstanbul", hc);
        for (int i = 0; i < 18; i++) {
            ht.addPlayer(new FootballPlayer("HTP" + i, "H" + i, 25,
                NameGenerator.getPositionForIndex(i), 70));
        }

        FootballCoach ac = new FootballCoach("CA2", "Koç A", 10, 7.0, "Balanced");
        FootballTeam at = new FootballTeam("TA2", "DepT", "Ankara", ac);
        for (int i = 0; i < 18; i++) {
            at.addPlayer(new FootballPlayer("ATP" + i, "A" + i, 25,
                NameGenerator.getPositionForIndex(i), 68));
        }

        Formation f = new FootballFormationFactory().createBalancedFormation();
        ht.setupLineup(f, new FootballSport.FootballBalancedTactic());
        at.setupLineup(f, new FootballSport.FootballBalancedTactic());

        FootballMatch m = new FootballMatch("MX", ht, at, LocalDateTime.now());
        m.simulateMatch();

        int homeInjured = 0;
        for (Player p : ht.getSquad()) {
            if (p != null && p.isInjured()) homeInjured++;
        }
        assertTrue(homeInjured >= 0 && homeInjured <= 2,
            "Ev sahibi sakatlık sayısı 0-2 arası olmalı: " + homeInjured);

        int awayInjured = 0;
        for (Player p : at.getSquad()) {
            if (p != null && p.isInjured()) awayInjured++;
        }
        assertTrue(awayInjured >= 0 && awayInjured <= 2,
            "Deplasman sakatlık sayısı 0-2 arası olmalı: " + awayInjured);

        for (Player p : ht.getSquad()) {
            if (p != null && p.isInjured()) {
                assertTrue(p.getInjuredMatchesRemaining() >= 1 && p.getInjuredMatchesRemaining() <= 5,
                    "Sakatlık süresi 1-5 arası olmalı: " + p.getInjuredMatchesRemaining());
            }
        }
    }

    @Test
    @DisplayName("Gol atanlar listesi")
    void testGoalScorers() {
        match.simulateMatch();
        List<Player> scorers = match.getGoalScorers();
        assertNotNull(scorers);
        assertEquals(match.getHomeScore() + match.getAwayScore(), scorers.size());
    }

    @Test
    @DisplayName("Maçın en değerli oyuncusu belirlenmeli")
    void testManOfTheMatch() {
        match.simulateMatch();
        MatchResult result = match.getMatchResult();
        Player mom = result.getManOfTheMatch();
        assertNotNull(mom);
    }

    @Test
    @DisplayName("Skor ekleme doğru çalışmalı")
    void testAddScore() {
        match.startMatch();

        Player homePlayer = homeTeam.getLineup()[2]; 
        if (homePlayer != null) {
            match.addScore(homeTeam, homePlayer);
            assertEquals(1, match.getHomeScore());
            assertEquals(0, match.getAwayScore());
        }

        Player awayPlayer = awayTeam.getLineup()[2];
        if (awayPlayer != null) {
            match.addScore(awayTeam, awayPlayer);
            assertEquals(1, match.getHomeScore());
            assertEquals(1, match.getAwayScore());
        }
    }
}
