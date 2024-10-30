package pacman.model.entity.dynamic.ghost;

public enum GhostType {
    BLINKY, PINKY, INKY, CLYDE;

    public static GhostType fromChar(char ghostChar) {
        return switch (ghostChar) {
            case 'b' -> BLINKY;
            case 's' -> PINKY;
            case 'i' -> INKY;
            case 'c' -> CLYDE;
            default -> null; // Or handle a default case as needed
        };
    }
}