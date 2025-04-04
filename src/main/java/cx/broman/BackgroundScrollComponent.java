package cx.broman;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.logging.Logger; // Import Logger
import com.almasb.fxgl.texture.Texture;

public class BackgroundScrollComponent extends Component {

    private static final Logger log = Logger.get(BackgroundScrollComponent.class); // Logger instance

    private double textureHeight;

    @Override
    public void onAdded() {
        // Get the texture height reliably from the Texture view
        Texture texture = entity.getViewComponent().getChildren().stream()
                .filter(node -> node instanceof Texture)
                .map(node -> (Texture) node)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Background entity must have a Texture view for entity: " + entity));
        
        this.textureHeight = texture.getHeight();

        if (this.textureHeight <= 0) {
            log.warning("Failed to get positive texture height from Texture object for entity: " + entity);
            // Attempt to use entity bounds if texture height failed (less reliable)
            this.textureHeight = entity.getHeight(); 
            if (this.textureHeight <= 0) {
                 throw new IllegalStateException("Could not determine background texture height for entity: " + entity);
            } else {
                 log.info("Using entity height as fallback: " + this.textureHeight + " for entity: " + entity);
            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (textureHeight <= 0) return; // Do nothing if height is invalid

        // Move the entity down
        // Pixels per second
        double scrollSpeed = 100.0;
        entity.translateY(scrollSpeed * tpf);

        // Log values for debugging
        // log.debug("Entity: " + entity + ", Y: " + entity.getY() + ", AppHeight: " + FXGL.getAppHeight() + ", TextureHeight: " + textureHeight); // Re-commented this log

        // Check if the entity's top edge has moved completely below the screen
        if (entity.getY() >= FXGL.getAppHeight()) {
            // If it has, move it back up by two texture heights relative to its current position.
            // This places it directly above where the other background entity *was* when this one went off-screen.
            double currentY = entity.getY();
            // Let's add a small buffer just in case of floating point inaccuracies
            double resetAmount = 2 * textureHeight;
            double newY = currentY - resetAmount;
            // log.debug("Resetting background entity " + entity + " from Y=" + currentY + " to Y=" + newY + " (TextureHeight=" + textureHeight + ", AppHeight=" + FXGL.getAppHeight() + ")"); // Keep this commented
            entity.setY(newY); // Use setY for absolute positioning might be safer than translate
        }
    }
}
