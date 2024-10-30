package pacman.model.factories;

import javafx.scene.image.Image;
import pacman.ConfigurationParseException;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.BoundingBoxImpl;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.staticentity.collectable.Pellet;

public class PowerPelletFactory implements RenderableFactory {
    private static final Image PELLET_IMAGE = new Image("maze/pellet.png");
    private static final int NUM_POINTS = 200;  // Adjusted score for power pellet
    private final Renderable.Layer layer = Renderable.Layer.BACKGROUND;
    private static final double SCALE_FACTOR = 2.0;

    @Override
    public Renderable createRenderable(Vector2D position) {
        try {
            // Apply offset and scaling for the power pellet
            Vector2D adjustedPosition = position.add(new Vector2D(-8, -8));  // Apply offset
            BoundingBox boundingBox = new BoundingBoxImpl(
                    adjustedPosition,
                    PELLET_IMAGE.getHeight() * SCALE_FACTOR,
                    PELLET_IMAGE.getWidth() * SCALE_FACTOR
            );

            return new Pellet(boundingBox, layer, PELLET_IMAGE, NUM_POINTS);

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid power pellet configuration | %s", e));
        }
    }
}
