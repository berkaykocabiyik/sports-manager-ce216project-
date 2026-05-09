package game;

import java.util.Arrays;

public class VolleyballFormationFactory implements FormationFactory {

    @Override
    public Formation createDefensiveFormation() {
        return createFormation("1 Setter - 2 Libero - 3 Smaçör");
    }

    @Override
    public Formation createBalancedFormation() {
        return createFormation("5-1");
    }

    @Override
    public Formation createOffensiveFormation() {
        return createFormation("6-2");
    }

    @Override
    public Formation createFormation(String name) {
        return switch (name) {
            case "1 Setter - 2 Libero - 3 Smaçör" -> new VolleyballFormation(
                name,
                "Savunma güvenliği yüksek diziliş",
                1,
                2,
                3,
                4,
                9
            );
            case "6-2" -> new VolleyballFormation(
                name,
                "İki pasörlü, hücum opsiyonu bol diziliş",
                2,
                0,
                4,
                9,
                4
            );
            case "5-1" -> new VolleyballFormation(
                name,
                "Tek pasörlü dengeli voleybol dizilişi",
                1,
                1,
                4,
                6,
                6
            );
            default -> throw new IllegalArgumentException("Bilinmeyen voleybol dizilişi: " + name);
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

    private static class VolleyballFormation implements Formation {

        private final String name;
        private final String description;
        private final int setters;
        private final int liberos;
        private final int spikers;
        private final int offensiveStrength;
        private final int defensiveStrength;

        VolleyballFormation(String name, String description, int setters, int liberos,
                            int spikers, int offensiveStrength, int defensiveStrength) {
            this.name = name;
            this.description = description;
            this.setters = setters;
            this.liberos = liberos;
            this.spikers = spikers;
            this.offensiveStrength = offensiveStrength;
            this.defensiveStrength = defensiveStrength;
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
            Position[] positions = new Position[getTotalPlayerCount()];
            int index = 0;
            for (int i = 0; i < setters; i++) positions[index++] = Position.SETTER;
            for (int i = 0; i < liberos; i++) positions[index++] = Position.LIBERO;
            for (int i = 0; i < spikers; i++) positions[index++] = Position.SPIKER;
            return positions;
        }

        @Override
        public int getPositionCount(Position position) {
            return switch (position) {
                case SETTER -> setters;
                case LIBERO -> liberos;
                case SPIKER -> spikers;
                default -> 0;
            };
        }

        @Override
        public int getTotalPlayerCount() {
            return setters + liberos + spikers;
        }

        @Override
        public int getOffensiveStrength() {
            return offensiveStrength;
        }

        @Override
        public int getDefensiveStrength() {
            return defensiveStrength;
        }

        @Override
        public String toString() {
            return name + " " + Arrays.toString(getRequiredPositions());
        }
    }
}
