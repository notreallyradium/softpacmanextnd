package pacman.model.entity.dynamic.ghost;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.level.Level;
import pacman.model.maze.Maze;

import java.util.*;

/**
 * Concrete implementation of Ghost entity in Pac-Man Game
 */
public class GhostImpl implements Ghost {

    private static final int minimumDirectionCount = 8;
    private final Layer layer = Layer.FOREGROUND;
    private final Image normalImage;
    private final Image frightenedImage = new Image("maze/ghosts/frightened.png");
    private Image currentImage;
    private final BoundingBox boundingBox;
    private final Vector2D startingPosition;
    private final Vector2D targetCorner; // Directly provided by GhostFactory
    private final char ghostType; // Ghost type identifier (e.g., 'b' for Blinky)
    private KinematicState kinematicState;
    private GhostMode ghostMode;
    private Vector2D targetLocation;
    private Vector2D playerPosition;
    private Direction currentDirection;
    private Set<Direction> possibleDirections;
    private Map<GhostMode, Double> speeds;
    private int currentDirectionCount = 0;
    private boolean isFrightened = false;
    private long frightenedEndTime;
    private Vector2D blinkyPosition;

    public GhostImpl(char ghostType, Image image, BoundingBox boundingBox, KinematicState kinematicState, GhostMode ghostMode, Vector2D targetCorner) {
        this.ghostType = ghostType;
        this.normalImage = image;
        this.currentImage = image;
        this.boundingBox = boundingBox;
        this.kinematicState = kinematicState;
        this.startingPosition = kinematicState.getPosition();
        this.ghostMode = ghostMode;
        this.possibleDirections = new HashSet<>();
        this.targetCorner = targetCorner;
        this.targetLocation = getTargetLocation();
        this.currentDirection = null;
    }

    @Override
    public void setSpeeds(Map<GhostMode, Double> speeds) {
        this.speeds = speeds;
    }

    @Override
    public Image getImage() {
        return currentImage;
    }

    @Override
    public void update() {
        if (isFrightened && System.currentTimeMillis() > frightenedEndTime) {
            exitFrightenedMode();
        }
        this.updateDirection();
        this.kinematicState.update();
        this.boundingBox.setTopLeft(this.kinematicState.getPosition());
    }

    private void updateDirection() {
        // Check if in FRIGHTENED mode and make random turn decisions
        if (this.ghostMode == GhostMode.FRIGHTENED) {
            List<Direction> availableDirections = new ArrayList<>(possibleDirections);

            // Filter out the opposite direction if other options are available
            if (availableDirections.size() > 1 && currentDirection != null) {
                availableDirections.remove(currentDirection.opposite());
            }

            // Choose a random direction from the available options
            this.currentDirection = availableDirections.get(new Random().nextInt(availableDirections.size()));
        } else {
            // Standard behavior for SCATTER and CHASE modes
            Direction newDirection = selectDirection(possibleDirections);

            // Ensure ghost continues in the current direction for a minimum time before changing
            if (this.currentDirection != newDirection) {
                this.currentDirectionCount = 0;
            }
            this.currentDirection = newDirection;
        }

        // Update movement based on the selected direction
        switch (currentDirection) {
            case LEFT -> this.kinematicState.left();
            case RIGHT -> this.kinematicState.right();
            case UP -> this.kinematicState.up();
            case DOWN -> this.kinematicState.down();
        }
    }


    public void setBlinkyPosition(Vector2D blinkyPosition) {
        this.blinkyPosition = blinkyPosition;
    }

    private Vector2D getTargetLocation() {
        return switch (this.ghostMode) {
            case CHASE -> calculateChaseTarget();
            case SCATTER, FRIGHTENED -> this.targetCorner;
        };
    }

    private Vector2D calculateChaseTarget() {
        // Logic based on ghost type to determine chase target location
        switch (ghostType) {
            case 'b': // Blinky (Shadow)
                return playerPosition;
            case 's': // Pinky (Speedy)
                return playerPosition.add(kinematicState.getDirection().toVector().scale(4 * 16));
            case 'c': // Clyde (Pokey)
                return Vector2D.calculateEuclideanDistance(startingPosition, playerPosition) > 8 * 16 ? playerPosition : targetCorner;
            case 'i': // Inky (Bashful)
                return calculateInkyTarget();
            default:
                return playerPosition; // Default to player position if ghost type is unrecognized
        }
    }

    private Vector2D calculateInkyTarget() {
        if (playerPosition != null && blinkyPosition != null) {
            Vector2D twoAhead = playerPosition.add(kinematicState.getDirection().toVector().scale(2 * 16));
            Vector2D vectorToDouble = twoAhead.subtract(blinkyPosition).scale(2);
            return blinkyPosition.add(vectorToDouble);
        }
        return targetCorner; // Default if necessary positions are unavailable
    }

    private Direction selectDirection(Set<Direction> possibleDirections) {
        if (possibleDirections.isEmpty()) {
            return currentDirection;
        }

        if (currentDirection != null && currentDirectionCount < minimumDirectionCount) {
            currentDirectionCount++;
            return currentDirection;
        }

        Map<Direction, Double> distances = new HashMap<>();

        for (Direction direction : possibleDirections) {
            if (currentDirection == null || direction != currentDirection.opposite()) {
                distances.put(direction, Vector2D.calculateEuclideanDistance(this.kinematicState.getPotentialPosition(direction), this.targetLocation));
            }
        }

        if (distances.isEmpty()) {
            return currentDirection.opposite();
        }

        return Collections.min(distances.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    @Override
    public GhostType getType() {
        return GhostType.fromChar(this.ghostType);
    }

    @Override
    public void setGhostMode(GhostMode ghostMode) {
        this.ghostMode = ghostMode;
        this.kinematicState.setSpeed(speeds.get(ghostMode));
        this.currentDirectionCount = minimumDirectionCount;
    }

    public void enterFrightenedMode(long duration) {
        this.ghostMode = GhostMode.FRIGHTENED;
        this.isFrightened = true;
        this.frightenedEndTime = System.currentTimeMillis() + duration;
        this.kinematicState.setSpeed(speeds.get(GhostMode.FRIGHTENED));
        this.currentImage = frightenedImage;
    }

    private void exitFrightenedMode() {
        this.isFrightened = false;
        setGhostMode(GhostMode.SCATTER);
        this.currentImage = normalImage;
    }

    @Override
    public boolean collidesWith(Renderable renderable) {
        return boundingBox.collidesWith(kinematicState.getSpeed(), kinematicState.getDirection(), renderable.getBoundingBox());
    }

    @Override
    public void collideWith(Level level, Renderable renderable) {
        if (level.isPlayer(renderable) && isFrightened) {
            level.handleLoseLife();
            reset();
        }
    }

    @Override
    public void update(Vector2D playerPosition) {
        this.playerPosition = playerPosition;
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public double getHeight() {
        return this.boundingBox.getHeight();
    }

    @Override
    public double getWidth() {
        return this.boundingBox.getWidth();
    }

    @Override
    public Vector2D getPosition() {
        return this.kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D position) {
        this.kinematicState.setPosition(position);
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public void reset() {
        this.kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                .setPosition(startingPosition)
                .build();
        this.boundingBox.setTopLeft(startingPosition);
        this.ghostMode = GhostMode.SCATTER;
        this.currentDirectionCount = minimumDirectionCount;
        this.isFrightened = false;
    }

    @Override
    public void setPossibleDirections(Set<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Direction getDirection() {
        return this.kinematicState.getDirection();
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(boundingBox.getMiddleX(), boundingBox.getMiddleY());
    }
}
