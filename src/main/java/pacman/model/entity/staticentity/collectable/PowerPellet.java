package pacman.model.entity.staticentity.collectable;

import javafx.scene.image.Image;
import pacman.model.entity.dynamic.physics.BoundingBox;

/**
 * Represents the Power Pellet in Pac-Man game
 */
public class PowerPellet extends Pellet {

    public PowerPellet(BoundingBox boundingBox, Layer layer, Image image, int points) {
        super(boundingBox, layer, image, points);
    }
}
