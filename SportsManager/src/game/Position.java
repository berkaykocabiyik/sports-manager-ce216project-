package game;

public enum Position {
    
    GOALKEEPER("Kaleci"),
    DEFENDER("Defender"),
    MIDFIELDER("Midfielder"),
    FORWARD("Forward"),
    
    SETTER("Setter"),
    LIBERO("Libero"),
    SPIKER("Spiker"),
    
    POINT_GUARD("Point Guard"),
    SHOOTING_GUARD("Shooting Guard"),
    SMALL_FORWARD("Small Forward"),
    POWER_FORWARD("Power Forward"),
    CENTER("Center");
    
    private final String displayName;
    
    Position(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
