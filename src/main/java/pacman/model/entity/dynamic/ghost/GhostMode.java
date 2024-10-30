package pacman.model.entity.dynamic.ghost;

/***
 * Represents the different modes of ghosts, which determines how ghosts choose their target locations
 */
public enum GhostMode {
    SCATTER,
    CHASE,
    FRIGHTENED; // Added FRIGHTENED mode

    /**
     * Ghosts alternate between SCATTER and CHASE mode normally
     *
     * @param ghostMode current ghost mode
     * @return next ghost mode
     */
    public static GhostMode getNextGhostMode(GhostMode ghostMode) {
        if (ghostMode == SCATTER) {
            return CHASE;
        } else if (ghostMode == CHASE) {
            return SCATTER;
        }
        return ghostMode; // Return same if FRIGHTENED (handled separately)
    }
}