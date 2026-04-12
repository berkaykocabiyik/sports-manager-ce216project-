package game;

public class FootballSport implements Sport {

    private static final String SPORT_NAME = "Futbol";
    private static final int PLAYER_COUNT = 11;
    private static final int SUBSTITUTE_COUNT = 7;
    private static final int MATCH_PERIODS = 2;
    private static final int PERIOD_DURATION = 45;
    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = Integer.MAX_VALUE;
    private static final int WIN_POINTS = 3;
    private static final int DRAW_POINTS = 1;
    private static final int LOSS_POINTS = 0;
    public static final int TEAM_COUNT = 18;
    public static final int SQUAD_SIZE = PLAYER_COUNT + SUBSTITUTE_COUNT;

    @Override
    public String getName() {
        return SPORT_NAME;
    }

    @Override
    public int getPlayerCount() {
        return PLAYER_COUNT;
    }

    @Override
    public int getSubstituteCount() {
        return SUBSTITUTE_COUNT;
    }

    @Override
    public int getMatchPeriods() {
        return MATCH_PERIODS;
    }

    @Override
    public int getPeriodDuration() {
        return PERIOD_DURATION;
    }

    @Override
    public int getMinimumScore() {
        return MIN_SCORE;
    }

    @Override
    public int getMaximumScore() {
        return MAX_SCORE;
    }

    @Override
    public int getWinPoints() {
        return WIN_POINTS;
    }

    @Override
    public int getDrawPoints() {
        return DRAW_POINTS;
    }

    @Override
    public int getLossPoints() {
        return LOSS_POINTS;
    }

    @Override
    public String getTiebreakerRule() {
        return "1. Puan, 2. Averaj (Atılan/Yenilen), 3. Gol Farkı, 4. Kura";
    }

    @Override
    public String getInjuryRule() {
        return "Maç sonunda 0-2 oyuncu sakatlanabilir (1-5 maç arası sakatlık süresi)";
    }

    @Override
    public Position[] getValidPositions() {
        return new Position[] {
                Position.GOALKEEPER,
                Position.DEFENDER,
                Position.MIDFIELDER,
                Position.FORWARD
        };
    }

    @Override
    public Tactic[] getValidTactics() {
        return new Tactic[] {
                new FootballDefensiveTactic(),
                new FootballBalancedTactic(),
                new FootballOffensiveTactic()
        };
    }

    @Override
    public FormationFactory getFormationFactory() {
        return new FootballFormationFactory();
    }

    @Override
    public boolean isValidMatchResult(int homeScore, int awayScore) {
        return homeScore >= MIN_SCORE && awayScore >= MIN_SCORE;
    }

    public static class FootballDefensiveTactic implements Tactic {
        @Override
        public String getName() {
            return "Defensive";
        }

        @Override
        public String getDescription() {
            return "Savunmacı ağırlıklı taktiğe sahip oyun";
        }

        @Override
        public String getSportType() {
            return "Futbol";
        }

        @Override
        public int getAggressivenessLevel() {
            return 3;
        }

        @Override
        public int getDefenseStrength() {
            return 9;
        }

        @Override
        public String[] getAdvantages() {
            return new String[] {
                    "Güçlü savunma",
                    "Kontra ataklara hazır",
                    "Gol yemenin azalması"
            };
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {
                    "Düşük hücum şansı",
                    "Pas oyununun kısıtlı olması",
                    "Rakip baskısına maruz kalma"
            };
        }
    }

    public static class FootballBalancedTactic implements Tactic {
        @Override
        public String getName() {
            return "Balanced";
        }

        @Override
        public String getDescription() {
            return "Dengeli hücum ve savunma oyunu";
        }

        @Override
        public String getSportType() {
            return "Futbol";
        }

        @Override
        public int getAggressivenessLevel() {
            return 5;
        }

        @Override
        public int getDefenseStrength() {
            return 5;
        }

        @Override
        public String[] getAdvantages() {
            return new String[] {
                    "Dengeli oyun",
                    "Esneklik",
                    "İyi kontrol"
            };
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {
                    "Açık stratejisi yok",
                    "Orta düzey performans",
                    "Net bir hedefe uyma zorluğu"
            };
        }
    }

    public static class FootballOffensiveTactic implements Tactic {
        @Override
        public String getName() {
            return "Offensive";
        }

        @Override
        public String getDescription() {
            return "Hücum ağırlıklı saldırgan oyun";
        }

        @Override
        public String getSportType() {
            return "Futbol";
        }

        @Override
        public int getAggressivenessLevel() {
            return 9;
        }

        @Override
        public int getDefenseStrength() {
            return 2;
        }

        @Override
        public String[] getAdvantages() {
            return new String[] {
                    "Yüksek gol şansı",
                    "Rakip baskısı",
                    "Hızlı ataklı oyun"
            };
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {
                    "Zayıf savunma",
                    "Kontra ataklara karşı duyarlı",
                    "Yüksek gol yeme riski"
            };
        }
    }
}