package game;

public class VolleyballSport implements Sport {

    public static final int TEAM_COUNT = 14;
    public static final int PLAYER_COUNT = 6;
    public static final int SUBSTITUTE_COUNT = 6;
    public static final int SQUAD_SIZE = PLAYER_COUNT + SUBSTITUTE_COUNT;

    @Override
    public String getName() {
        return "Voleybol";
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
        return 5;
    }

    @Override
    public int getPeriodDuration() {
        return 25;
    }

    @Override
    public int getMinimumScore() {
        return 0;
    }

    @Override
    public int getMaximumScore() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getWinPoints() {
        return 3;
    }

    @Override
    public int getDrawPoints() {
        return 0;
    }

    @Override
    public int getLossPoints() {
        return 0;
    }

    @Override
    public String getTiebreakerRule() {
        return "1. Galibiyet, 2. Set averajı, 3. Set farkı, 4. Sayı farkı";
    }

    @Override
    public String getInjuryRule() {
        return "Maç sonunda 0-2 oyuncu sakatlanabilir (1-5 maç arası).";
    }

    @Override
    public Position[] getValidPositions() {
        return new Position[] {
            Position.SETTER,
            Position.LIBERO,
            Position.SPIKER
        };
    }

    @Override
    public Tactic[] getValidTactics() {
        return new Tactic[] {
            new ServePressureTactic(),
            new BalancedVolleyTactic(),
            new BlockDefenseTactic()
        };
    }

    @Override
    public FormationFactory getFormationFactory() {
        return new VolleyballFormationFactory();
    }

    @Override
    public boolean isValidMatchResult(int homeScore, int awayScore) {
        return (homeScore == 3 && awayScore >= 0 && awayScore <= 2)
            || (awayScore == 3 && homeScore >= 0 && homeScore <= 2);
    }

    public static class ServePressureTactic implements Tactic {
        @Override
        public String getName() {
            return "Servis Baskısı";
        }

        @Override
        public String getDescription() {
            return "Riskli servislerle rakibi karşılama hatasına zorlar.";
        }

        @Override
        public String getSportType() {
            return "Voleybol";
        }

        @Override
        public int getAggressivenessLevel() {
            return 8;
        }

        @Override
        public int getDefenseStrength() {
            return 4;
        }

        @Override
        public String[] getAdvantages() {
            return new String[] {"Ace şansı", "Rakip oyun kurulumunu bozma"};
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {"Servis hatası riski", "Uzun rallilerde savunma açığı"};
        }
    }

    public static class BalancedVolleyTactic implements Tactic {
        @Override
        public String getName() {
            return "Dengeli Oyun";
        }

        @Override
        public String getDescription() {
            return "Servis, blok ve hücumu dengeli kullanan standart oyun.";
        }

        @Override
        public String getSportType() {
            return "Voleybol";
        }

        @Override
        public int getAggressivenessLevel() {
            return 5;
        }

        @Override
        public int getDefenseStrength() {
            return 6;
        }

        @Override
        public String[] getAdvantages() {
            return new String[] {"Düşük hata oranı", "Esnek oyun planı"};
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {"Belirgin üstünlük kurması zor"};
        }
    }

    public static class BlockDefenseTactic implements Tactic {
        @Override
        public String getName() {
            return "Blok Savunması";
        }

        @Override
        public String getDescription() {
            return "Rakip smaçlarını blok ve savunma yerleşimiyle durdurur.";
        }

        @Override
        public String getSportType() {
            return "Voleybol";
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
            return new String[] {"Güçlü blok", "Daha az sayı yeme"};
        }

        @Override
        public String[] getDisadvantages() {
            return new String[] {"Hücum temposu düşebilir"};
        }
    }
}
