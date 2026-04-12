package game;

public class FootballFormationFactory implements FormationFactory {

    @Override
    public Formation createDefensiveFormation() {
        
        return new FootballFormation("4-4-2",
            "4 Defans, 4 Orta Saha, 2 Forvet - Dengeli savunma",
            1, 4, 4, 2, 7, 5);
    }

    @Override
    public Formation createBalancedFormation() {
        
        return new FootballFormation("4-3-3",
            "4 Defans, 3 Orta Saha, 3 Forvet - Dengeli hücum",
            1, 4, 3, 3, 5, 7);
    }

    @Override
    public Formation createOffensiveFormation() {
        
        return new FootballFormation("3-5-2",
            "3 Defans, 5 Orta Saha, 2 Forvet - Hücum ağırlıklı",
            1, 3, 5, 2, 4, 8);
    }

    @Override
    public Formation createFormation(String name) {
        return switch (name) {
            case "4-4-2" -> createDefensiveFormation();
            case "4-3-3" -> createBalancedFormation();
            case "3-5-2" -> createOffensiveFormation();
            default -> createBalancedFormation();
        };
    }

    @Override
    public Formation[] getAllFormations() {
        return new Formation[] {
            createDefensiveFormation(),
            createBalancedFormation(),
            createOffensiveFormation()
        };
    }

    static class FootballFormation implements Formation {

        private final String name;
        private final String description;
        private final int goalkeepers;
        private final int defenders;
        private final int midfielders;
        private final int forwards;
        private final int defensiveStrength;
        private final int offensiveStrength;

        public FootballFormation(String name, String description,
                                 int goalkeepers, int defenders, int midfielders, int forwards,
                                 int defensiveStrength, int offensiveStrength) {
            this.name = name;
            this.description = description;
            this.goalkeepers = goalkeepers;
            this.defenders = defenders;
            this.midfielders = midfielders;
            this.forwards = forwards;
            this.defensiveStrength = defensiveStrength;
            this.offensiveStrength = offensiveStrength;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Position[] getRequiredPositions() {
            return new Position[] {
                Position.GOALKEEPER,
                Position.DEFENDER,
                Position.MIDFIELDER,
                Position.FORWARD
            };
        }

        @Override
        public int getPositionCount(Position position) {
            return switch (position) {
                case GOALKEEPER -> goalkeepers;
                case DEFENDER -> defenders;
                case MIDFIELDER -> midfielders;
                case FORWARD -> forwards;
                default -> 0;
            };
        }

        @Override
        public int getTotalPlayerCount() {
            return goalkeepers + defenders + midfielders + forwards; 
        }

        @Override
        public int getOffensiveStrength() {
            return offensiveStrength;
        }

        @Override
        public int getDefensiveStrength() {
            return defensiveStrength;
        }
    }
}
