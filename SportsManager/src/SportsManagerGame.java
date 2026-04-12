import game.*;

public class SportsManagerGame {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║           ⚽ FUTBOL LİGİ SİMÜLASYONU ⚽               ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        Sport sport = new FootballSport();
        System.out.println("Spor: " + sport.getName());
        System.out.println("Oyuncu Sayısı: " + sport.getPlayerCount() + " asil + " +
            sport.getSubstituteCount() + " yedek");
        System.out.println("Puan Sistemi: G=" + sport.getWinPoints() +
            "p, B=" + sport.getDrawPoints() + "p, M=" + sport.getLossPoints() + "p");
        System.out.println("Sıralama: " + sport.getTiebreakerRule());
        System.out.println("Sakatlık: " + sport.getInjuryRule());
        System.out.println();

        FootballLeague league = new FootballLeague("L1", "Süper Lig 2024-2025");

        league.addObserver(new LeagueStatusObserver());

        System.out.println("=== TAKIMLAR OLUŞTURULUYOR ===\n");
        for (int t = 0; t < FootballSport.TEAM_COUNT; t++) {
            String teamName = NameGenerator.getTeamName(t);
            String city = NameGenerator.getCityName(t);

            String coachName = NameGenerator.generateCoachName();
            int coachExp = NameGenerator.generateCoachExperience();
            int coachRating = NameGenerator.generateCoachRating();
            Coach coach = new FootballCoach(
                "C" + t,
                coachName,
                coachExp,
                coachRating / 10.0,
                NameGenerator.getRandomSpecialty()
            );

            FootballTeam team = new FootballTeam("T" + t, teamName, city, coach);

            for (int p = 0; p < FootballSport.SQUAD_SIZE; p++) {
                String playerName = NameGenerator.generatePlayerName();
                int age = NameGenerator.generatePlayerAge();
                int ovr = NameGenerator.generateOVR();
                Position position = NameGenerator.getPositionForIndex(p);

                FootballPlayer player = new FootballPlayer(
                    "T" + t + "P" + p,
                    playerName,
                    age,
                    position,
                    ovr
                );
                team.addPlayer(player);
            }

            league.addTeam(team);
            System.out.printf("  %2d. %-20s (%-12s) | Koç: %-25s | Kadro: %d oyuncu%n",
                t + 1, teamName, city, coachName, team.getSquadSize());
        }
        System.out.println();

        System.out.println("=== ÖRNEK KADRO DETAYLARI (İlk 3 Takım) ===\n");
        java.util.List<Team> activeTeams = league.getActiveTeams();
        for (int t = 0; t < Math.min(3, activeTeams.size()); t++) {
            Team team = activeTeams.get(t);
            System.out.println("--- " + team.getName() + " (" + team.getCity() + ") ---");
            System.out.println("Koç: " + team.getCoach().getName() +
                " | Deneyim: " + team.getCoach().getExperience() + " yıl" +
                " | Rating: " + String.format("%.1f", team.getCoach().getCoachingRating()));
            System.out.printf("Takım Gücü: %.2f/10%n", team.calculateTeamStrength());

            for (Player p : team.getSquad()) {
                if (p instanceof FootballPlayer fp) {
                    System.out.printf("  %-25s | %-12s | Yaş: %2d | OVR: %2d%n",
                        fp.getName(),
                        fp.getPosition().getDisplayName(),
                        fp.getAge(),
                        fp.getOverallRating());
                }
            }
            System.out.println();
        }

        System.out.println("=== FİKSTÜR OLUŞTURULUYOR ===\n");
        league.generateFixture();
        System.out.println("Toplam hafta: " + league.getFixture().getTotalWeeks());
        System.out.println("Toplam maç: " + league.getFixture().getAllMatches().length);
        System.out.println();

        System.out.println("=== SEZON BAŞLIYOR ===\n");
        System.out.println("Not: Maç detayları gizleniyor, sadece haftalık sonuçlar gösterilecek...\n");

        league.clearObservers();
        league.addObserver(new SilentLeagueObserver());

        if (league.getFixture() != null) {
            int totalWeeks = league.getFixture().getTotalWeeks();

            for (int week = 1; week <= totalWeeks; week++) {
                Match[] weekMatches = league.getFixture().getWeekMatches(week);

                System.out.println("━━━ Hafta " + week + "/" + totalWeeks + " ━━━");
                for (Match match : weekMatches) {
                    if (match.getStatus() == MatchStatus.SCHEDULED) {
                        league.playMatch(match);
                        MatchResult result = match.getMatchResult();
                        System.out.printf("  %-20s %d-%d %-20s%n",
                            result.getHomeTeam().getName(),
                            result.getHomeScore(),
                            result.getAwayScore(),
                            result.getAwayTeam().getName());
                    }
                }
                System.out.println();
            }
        }

        System.out.println("\n=== SEZON SONU - FİNAL PUAN TABLOSU ===");
        league.updateStandings();
        league.getStandings().printTable();

        Team[] finalRanking = league.getStandings().getRankedTeams();
        if (finalRanking.length > 0) {
            System.out.println("🏆 ŞAMPİYON: " + finalRanking[0].getName() +
                " (" + league.getStandings().getTeamPoints(finalRanking[0]) + " puan)");
        }
        if (finalRanking.length > 1) {
            System.out.println("🥈 2. : " + finalRanking[1].getName() +
                " (" + league.getStandings().getTeamPoints(finalRanking[1]) + " puan)");
        }
        if (finalRanking.length > 2) {
            System.out.println("🥉 3. : " + finalRanking[2].getName() +
                " (" + league.getStandings().getTeamPoints(finalRanking[2]) + " puan)");
        }

        System.out.println("\n=== GOL KRALI ===");
        FootballPlayer topScorer = null;
        String topScorerTeam = "";
        int maxGoals = 0;
        for (Team team : activeTeams) {
            for (Player p : team.getSquad()) {
                if (p instanceof FootballPlayer fp && fp.getGoals() > maxGoals) {
                    maxGoals = fp.getGoals();
                    topScorer = fp;
                    topScorerTeam = team.getName();
                }
            }
        }
        if (topScorer != null) {
            System.out.printf("⚽ %s (%s) - %d gol%n",
                topScorer.getName(), topScorerTeam, topScorer.getGoals());
        }

        System.out.println("\n=== ASİST KRALI ===");
        FootballPlayer topAssister = null;
        String topAssisterTeam = "";
        int maxAssists = 0;
        for (Team team : activeTeams) {
            for (Player p : team.getSquad()) {
                if (p instanceof FootballPlayer fp && fp.getAssists() > maxAssists) {
                    maxAssists = fp.getAssists();
                    topAssister = fp;
                    topAssisterTeam = team.getName();
                }
            }
        }
        if (topAssister != null) {
            System.out.printf("🅰️ %s (%s) - %d asist%n",
                topAssister.getName(), topAssisterTeam, topAssister.getAssists());
        }

        System.out.println("\n✅ Simülasyon tamamlandı!");
    }
}

class SilentLeagueObserver implements LeagueObserver {
    @Override
    public void leagueUpdated(League league, String event) {
        
    }
}

class LeagueStatusObserver implements LeagueObserver {
    @Override
    public void leagueUpdated(League league, String event) {
        System.out.println("[LİG] " + event);
    }
}

class GameScoreboard implements MatchObserver {
    @Override
    public void update(Match match, String event) {
        System.out.println("[SKOR] " + event);
    }
}

class GameStatistics implements MatchObserver {
    @Override
    public void update(Match match, String event) {
        System.out.println("[İSTAT] " + event);
    }
}
