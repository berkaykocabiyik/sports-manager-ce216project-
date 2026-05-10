package game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class GameFactory {

    public static final List<String> DEFAULT_STUDENT_IDS = Arrays.asList(
        "20210602036",
        "20200602038",
        "20240602056",
        "20220602008"
    );

    private static final String[] VOLLEYBALL_TEAM_NAMES = {
        "Altekma",
        "Bursa Büyükşehir Bld.",
        "Rams Global Cizre Bld.",
        "Fenerbahçe Medicana",
        "Galatasaray HDI Sigorta",
        "Gaziantep Gençlik Spor",
        "Gebze Bld.",
        "Halkbank",
        "İBB Spor Kulübü",
        "İstanbul Gençlik",
        "Kuşgöz İzmir Vinç Akkuş Belediyespor",
        "On Hotels Alanya Bld.",
        "Spor Toto",
        "Ziraat Bankkart"
    };

    private static final String[] VOLLEYBALL_CITIES = {
        "İzmir",
        "Bursa",
        "Şırnak",
        "İstanbul",
        "İstanbul",
        "Gaziantep",
        "Kocaeli",
        "Ankara",
        "İstanbul",
        "İstanbul",
        "Ordu",
        "Antalya",
        "Ankara",
        "Ankara"
    };

    private static final String[] FOOTBALL_TEAM_NAMES = {
        "Alanyaspor",
        "Antalyaspor",
        "İstanbul Başakşehir",
        "Beşiktaş",
        "Eyüpspor",
        "Fatih Karagümrük",
        "Fenerbahçe",
        "Galatasaray",
        "Gaziantep FK",
        "Gençlerbirliği",
        "Göztepe",
        "Kasımpaşa",
        "Kayserispor",
        "Kocaelispor",
        "Konyaspor",
        "Çaykur Rizespor",
        "Samsunspor",
        "Trabzonspor"
    };

    private static final String[] FOOTBALL_CITIES = {
        "Antalya",
        "Antalya",
        "İstanbul",
        "İstanbul",
        "İstanbul",
        "İstanbul",
        "İstanbul",
        "İstanbul",
        "Gaziantep",
        "Ankara",
        "İzmir",
        "İstanbul",
        "Kayseri",
        "Kocaeli",
        "Konya",
        "Rize",
        "Samsun",
        "Trabzon"
    };

    private static final String[] SPECIALTIES = {
        "Servis Baskısı", "Blok Savunması", "Dengeli Oyun", "Genç Oyuncu Gelişimi"
    };

    private static final Random RANDOM = new Random();

    private GameFactory() {
    }

    public static GameState createDefaultGameState() {
        SportType selectedSport = StudentSportSelector.selectFromStudentIds(DEFAULT_STUDENT_IDS);
        if (selectedSport != SportType.VOLLEYBALL) {
            throw new IllegalStateException("Bu proje teslimi voleybol için yapılandırıldı.");
        }

        return createGameState(selectedSport);
    }

    public static GameState createGameState(SportType selectedSport) {
        if (selectedSport == SportType.FOOTBALL) {
            FootballLeague league = createFootballLeague();
            GameState state = new GameState(SportType.FOOTBALL, league);
            state.addRecentResult("İlk spor olarak Futbol seçildi.");
            return state;
        }
        if (selectedSport != SportType.VOLLEYBALL) {
            throw new IllegalArgumentException("Bu teslimde yalnızca ilk spor Futbol ve ikinci spor Voleybol uygulanır.");
        }
        VolleyballLeague league = createVolleyballLeague();
        GameState state = new GameState(selectedSport, league);
        state.addRecentResult("Öğrenci numarası hesabı: 28 mod 3 = 1, seçilen spor Voleybol.");
        return state;
    }

    public static FootballLeague createFootballLeague() {
        FootballLeague league = new FootballLeague("FL1", "CE216 Futbol Ligi");
        for (int i = 0; i < FootballSport.TEAM_COUNT; i++) {
            league.addTeam(createFootballTeam(i));
        }
        league.generateFixture();
        league.updateStandings();
        return league;
    }

    public static VolleyballLeague createVolleyballLeague() {
        VolleyballLeague league = new VolleyballLeague("VL1", "CE216 Voleybol Ligi");
        for (int i = 0; i < VolleyballSport.TEAM_COUNT; i++) {
            league.addTeam(createVolleyballTeam(i));
        }
        league.generateFixture();
        league.updateStandings();
        return league;
    }

    private static VolleyballTeam createVolleyballTeam(int index) {
        VolleyballCoach coach = new VolleyballCoach(
            "VC" + index,
            NameGenerator.generateCoachName(),
            NameGenerator.generateCoachExperience(),
            NameGenerator.generateCoachRating() / 10.0,
            SPECIALTIES[index % SPECIALTIES.length]
        );

        VolleyballTeam team = new VolleyballTeam(
            "VT" + index,
            VOLLEYBALL_TEAM_NAMES[index],
            VOLLEYBALL_CITIES[index],
            coach
        );

        for (int playerIndex = 0; playerIndex < VolleyballSport.SQUAD_SIZE; playerIndex++) {
            team.addPlayer(new VolleyballPlayer(
                team.getId() + "P" + playerIndex,
                NameGenerator.generatePlayerName(),
                NameGenerator.generatePlayerAge(),
                volleyballPositionForIndex(playerIndex),
                52 + RANDOM.nextInt(44)
            ));
        }

        Formation formation = coach.selectFormation(team);
        Tactic tactic = new VolleyballSport.BalancedVolleyTactic();
        team.setupLineup(formation, tactic);
        return team;
    }

    private static FootballTeam createFootballTeam(int index) {
        FootballCoach coach = new FootballCoach(
            "FC" + index,
            NameGenerator.generateCoachName(),
            NameGenerator.generateCoachExperience(),
            NameGenerator.generateCoachRating() / 10.0,
            NameGenerator.getRandomSpecialty()
        );

        FootballTeam team = new FootballTeam(
            "FT" + index,
            FOOTBALL_TEAM_NAMES[index],
            FOOTBALL_CITIES[index],
            coach
        );

        for (int playerIndex = 0; playerIndex < FootballSport.SQUAD_SIZE; playerIndex++) {
            team.addPlayer(new FootballPlayer(
                team.getId() + "P" + playerIndex,
                NameGenerator.generatePlayerName(),
                NameGenerator.generatePlayerAge(),
                NameGenerator.getPositionForIndex(playerIndex),
                52 + RANDOM.nextInt(44)
            ));
        }

        Formation formation = coach.selectFormation(team);
        Tactic tactic = new FootballSport.FootballBalancedTactic();
        team.setupLineup(formation, tactic);
        return team;
    }

    public static Position volleyballPositionForIndex(int index) {
        if (index < 2) return Position.SETTER;
        if (index < 4) return Position.LIBERO;
        return Position.SPIKER;
    }
}
