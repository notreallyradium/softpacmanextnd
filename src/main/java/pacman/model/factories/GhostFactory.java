package pacman.model.factories;

import javafx.scene.image.Image;
import pacman.ConfigurationParseException;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.ghost.GhostMode;
import pacman.model.entity.dynamic.physics.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Concrete renderable factory for Ghost objects
 */
public class GhostFactory implements RenderableFactory {

    private static final int RIGHT_X_POSITION_OF_MAP = 448;
    private static final int TOP_Y_POSITION_OF_MAP = 16 * 3;
    private static final int BOTTOM_Y_POSITION_OF_MAP = 16 * 34;

    private static final Image BLINKY_IMAGE = new Image("maze/ghosts/blinky.png");
    private static final Image INKY_IMAGE = new Image("maze/ghosts/inky.png");
    private static final Image CLYDE_IMAGE = new Image("maze/ghosts/clyde.png");
    private static final Image PINKY_IMAGE = new Image("maze/ghosts/pinky.png");

    private final char ghostType;
    private final Random random = new Random();

    private static final List<Vector2D> targetCorners = Arrays.asList(
            new Vector2D(0, TOP_Y_POSITION_OF_MAP),
            new Vector2D(RIGHT_X_POSITION_OF_MAP, TOP_Y_POSITION_OF_MAP),
            new Vector2D(0, BOTTOM_Y_POSITION_OF_MAP),
            new Vector2D(RIGHT_X_POSITION_OF_MAP, BOTTOM_Y_POSITION_OF_MAP)
    );

    public GhostFactory(char ghostType) {
        this.ghostType = ghostType;
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public Renderable createRenderable(Vector2D position) {
        try {
            // Choose the correct image based on the ghost type
            Image ghostImage;
            switch (ghostType) {
                case 'b' -> ghostImage = BLINKY_IMAGE;
                case 's' -> ghostImage = PINKY_IMAGE;
                case 'i' -> ghostImage = INKY_IMAGE;
                case 'c' -> ghostImage = CLYDE_IMAGE;
                default -> ghostImage = BLINKY_IMAGE; // Default to Blinky if type is unrecognized
            }

            BoundingBox boundingBox = new BoundingBoxImpl(
                    position,
                    ghostImage.getHeight(),
                    ghostImage.getWidth()
            );

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(position)
                    .setDirection(Direction.values()[random.nextInt(Direction.values().length)]) // Random initial direction
                    .build();

            return new GhostImpl(
                    ghostType,
                    ghostImage,
                    boundingBox,
                    kinematicState,
                    GhostMode.SCATTER,
                    targetCorners.get(getRandomNumber(0, targetCorners.size()))
            );
        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid ghost configuration for type %s | %s", ghostType, e));
        }
    }
}