import game.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FootballLeague Testleri")
class FootballLeagueTest {

    private FootballLeague league;

    @BeforeEach
    void setUp() {
        league = new FootballLeague("L1", "Test Lig");
    }

    private FootballTeam createTeam(String id, String name) {
        FootballCoach coach = new FootballCoach("C" + id, "Koç " + name,
            10, 7.0, "Balanced");
        FootballTeam team = new FootballTeam(id, name, "Şehir", coach);
        for (int i = 0; i < 18; i++) {
            Position pos = NameGenerator.getPositionForIndex(i);
            team.addPlayer(new FootballPlayer(id + "P" + i, name + " Oyuncu " + i,
                25, pos, 60 + i));
        }
        return team;
    }

    @Test
    @DisplayName("Lig bilgileri doğru olmalı")
    void testLeagueInfo() {
        assertEquals("L1", league.getId());
        assertEquals("Test Lig", league.getName());
        assertEquals("Futbol", league.getSport().getName());
    }

    @Test
    @DisplayName("Takım ekleme")
    void testAddTeam() {
        FootballTeam team = createTeam("T1", "Takım 1");
        league.addTeam(team);
        assertEquals(1, league.getTeamCount());
    }

    @Test
    @DisplayName("Maksimum 20 takım eklenebilir")
    void testMaxTeamLimit() {
        for (int i = 0; i < 20; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }
        assertEquals(20, league.getTeamCount());

        assertThrows(RuntimeException.class, () -> {
            league.addTeam(createTeam("T20", "Fazla Takım"));
        });
    }

    @Test
    @DisplayName("Takım kaldırma")
    void testRemoveTeam() {
        FootballTeam team = createTeam("T1", "Takım 1");
        league.addTeam(team);
        assertEquals(1, league.getTeamCount());

        league.removeTeam("T1");
        assertEquals(0, league.getTeamCount());
    }

    @Test
    @DisplayName("Çift devreli fikstür oluşturma - 4 takım")
    void testDoubleRoundRobinFixture4Teams() {
        for (int i = 0; i < 4; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }

        league.generateFixture();

        assertNotNull(league.getFixture());
        
        assertEquals(12, league.getFixture().getAllMatches().length);
    }

    @Test
    @DisplayName("Çift devreli fikstür - her takım diğerleriyle 2'şer kez oynamalı")
    void testEachTeamPlays2TimesAgainstOthers() {
        int teamCount = 4;
        for (int i = 0; i < teamCount; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }
        league.generateFixture();

        List<Team> teams = league.getActiveTeams();
        for (Team team : teams) {
            Match[] teamMatches = league.getFixture().getTeamMatches(team);
            
            assertEquals((teamCount - 1) * 2, teamMatches.length,
                team.getName() + " yanlış sayıda maç: " + teamMatches.length);
        }
    }

    @Test
    @DisplayName("Fikstür hafta bazlı maçlar döndürmeli")
    void testWeeklyMatches() {
        for (int i = 0; i < 4; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }
        league.generateFixture();

        int totalWeeks = league.getFixture().getTotalWeeks();
        assertTrue(totalWeeks > 0);

        Match[] week1 = league.getFixture().getWeekMatches(1);
        assertTrue(week1.length > 0);
    }

    @Test
    @DisplayName("Maç oynama ve puan tablosu güncelleme")
    void testPlayMatchAndUpdateStandings() {
        FootballTeam team1 = createTeam("T1", "Takım 1");
        FootballTeam team2 = createTeam("T2", "Takım 2");
        league.addTeam(team1);
        league.addTeam(team2);

        league.generateFixture();

        Match[] allMatches = league.getFixture().getAllMatches();
        assertTrue(allMatches.length > 0);

        league.playMatch(allMatches[0]);

        assertNotNull(league.getStandings());
        Team[] ranked = league.getStandings().getRankedTeams();
        assertEquals(2, ranked.length);
    }

    @Test
    @DisplayName("Puan tablosu - puan sıralaması")
    void testStandingsPointOrder() {
        FootballTeam team1 = createTeam("T1", "Takım 1");
        FootballTeam team2 = createTeam("T2", "Takım 2");

        team1.recordMatchResult(3, 0, true, false); 
        team2.recordMatchResult(0, 3, false, false); 

        league.addTeam(team1);
        league.addTeam(team2);
        league.updateStandings();

        Team[] ranked = league.getStandings().getRankedTeams();
        assertEquals("Takım 1", ranked[0].getName()); 
        assertEquals("Takım 2", ranked[1].getName());
    }

    @Test
    @DisplayName("Puan tablosu - averaj sıralaması")
    void testStandingsGoalAverageOrder() {
        FootballTeam team1 = createTeam("T1", "Takım 1");
        FootballTeam team2 = createTeam("T2", "Takım 2");

        team1.recordMatchResult(4, 2, true, false); 
        team2.recordMatchResult(2, 1, true, false); 

        team1.recordMatchResult(1, 1, false, true); 
        team2.recordMatchResult(3, 1, true, false); 

        league.addTeam(team1);
        league.addTeam(team2);
        league.updateStandings();

        Team[] ranked = league.getStandings().getRankedTeams();
        assertEquals(4, league.getStandings().getTeamPoints(team1));
        assertEquals(6, league.getStandings().getTeamPoints(team2));
    }

    @Test
    @DisplayName("Puan tablosu gösterimi boş olmamalı")
    void testStandingsDisplay() {
        for (int i = 0; i < 4; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }
        league.updateStandings();

        String display = league.getStandings().getTableDisplay();
        assertNotNull(display);
        assertFalse(display.isEmpty());
        assertTrue(display.contains("PUAN TABLOSU"));
    }

    @Test
    @DisplayName("Standings - takım rank")
    void testTeamRank() {
        FootballTeam team1 = createTeam("T1", "Birinci");
        FootballTeam team2 = createTeam("T2", "İkinci");

        team1.recordMatchResult(5, 0, true, false); 
        team2.recordMatchResult(0, 5, false, false); 

        league.addTeam(team1);
        league.addTeam(team2);
        league.updateStandings();

        assertEquals(1, league.getStandings().getTeamRank(team1));
        assertEquals(2, league.getStandings().getTeamRank(team2));
    }

    @Test
    @DisplayName("Standings - istatistikler")
    void testStandingsStats() {
        FootballTeam team = createTeam("T1", "Test");
        team.recordMatchResult(3, 1, true, false);
        team.recordMatchResult(0, 0, false, true);
        team.recordMatchResult(1, 2, false, false);

        league.addTeam(team);
        league.updateStandings();

        Standings s = league.getStandings();
        assertEquals(3, s.getTeamMatchesPlayed(team));
        assertEquals(1, s.getTeamWins(team));
        assertEquals(1, s.getTeamDraws(team));
        assertEquals(1, s.getTeamLosses(team));
        assertEquals(4, s.getTeamPoints(team)); 
    }

    @Test
    @DisplayName("League observer pattern")
    void testLeagueObserver() {
        final int[] updateCount = {0};
        LeagueObserver observer = (l, event) -> updateCount[0]++;

        league.addObserver(observer);
        league.addTeam(createTeam("T1", "Takım 1"));

        assertTrue(updateCount[0] > 0);
    }

    @Test
    @DisplayName("Aktif takım listesi")
    void testActiveTeams() {
        for (int i = 0; i < 5; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }

        List<Team> active = league.getActiveTeams();
        assertEquals(5, active.size());
    }

    @Test
    @DisplayName("Oynanan maçlar listesi")
    void testPlayedMatches() {
        FootballTeam t1 = createTeam("T1", "Takım 1");
        FootballTeam t2 = createTeam("T2", "Takım 2");
        league.addTeam(t1);
        league.addTeam(t2);
        league.generateFixture();

        Match[] allMatches = league.getFixture().getAllMatches();
        league.playMatch(allMatches[0]);

        assertEquals(1, league.getPlayedMatches().size());
    }

    @Test
    @DisplayName("Fikstür tarih aralığı doğru olmalı")
    void testFixtureDateRange() {
        for (int i = 0; i < 4; i++) {
            league.addTeam(createTeam("T" + i, "Takım " + i));
        }
        league.generateFixture();

        assertNotNull(league.getFixture().getStartDate());
        assertNotNull(league.getFixture().getEndDate());
        assertTrue(league.getFixture().getEndDate().isAfter(league.getFixture().getStartDate()) ||
                   league.getFixture().getEndDate().isEqual(league.getFixture().getStartDate()));
    }

    @Test
    @DisplayName("Sonraki maç sorgusu")
    void testNextMatch() {
        FootballTeam t1 = createTeam("T1", "Takım 1");
        FootballTeam t2 = createTeam("T2", "Takım 2");
        league.addTeam(t1);
        league.addTeam(t2);
        league.generateFixture();

        Match next = league.getFixture().getNextMatch(t1);
        assertNotNull(next);
        assertEquals(MatchStatus.SCHEDULED, next.getStatus());
    }
}
