package game;

public enum SportType {
    FOOTBALL("Futbol"),
    BASKETBALL("Basketbol"),
    VOLLEYBALL("Voleybol"),
    HANDBALL("Hentbol");

    private final String displayName;

    SportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
