package cx.broman;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Rectangle2D; // Import needed

import java.util.List; // Import needed

import static com.almasb.fxgl.dsl.FXGL.*;

public class AsteroidComponent extends Component {

    private static final int MAX_SPAWN_ATTEMPTS = 10; // Limit attempts to find a clear spot

    public void move() {
        double speed = 50;
        entity.translateY(speed * tpf());
    }

    public void resetPosition() {
        double asteroidWidth = entity.getWidth();
        double asteroidHeight = entity.getHeight(); // Use actual height for Y position and overlap check
        double maxSpawnX = getAppWidth() - asteroidWidth; // Ensure right edge is within bounds

        if (maxSpawnX < 0) {
            maxSpawnX = 0; // Handle cases where asteroid might be wider than screen
        }

        double spawnY = -asteroidHeight - 10; // Place just above the screen

        for (int attempts = 0; attempts < MAX_SPAWN_ATTEMPTS; attempts++) {
            double potentialX = random(0, maxSpawnX); // Generate X within valid bounds
            Rectangle2D potentialBounds = new Rectangle2D(potentialX, spawnY, asteroidWidth, asteroidHeight);

            // Check for overlap with other existing asteroids
            List<Entity> overlappingAsteroids = getGameWorld().getEntitiesInRange(potentialBounds)
                    .stream()
                    .filter(e -> e != entity && e.isType(EntityType.ASTEROID)) // Exclude self, check type
                    .toList();

            if (overlappingAsteroids.isEmpty()) {
                // Found a clear spot
                entity.setPosition(potentialX, spawnY);
                return; // Exit the method
            }
        }

        // If max attempts reached, just place it randomly (might overlap, but avoids infinite loop)
        // Alternatively, could choose not to spawn, but this keeps the asteroid count consistent.
        entity.setPosition(random(0, maxSpawnX), spawnY);
        System.err.println("Warning: Could not find non-overlapping spawn position for asteroid after " + MAX_SPAWN_ATTEMPTS + " attempts. Placing randomly.");
    }
}
