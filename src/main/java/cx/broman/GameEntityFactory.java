package cx.broman;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent; // Import needed
import com.almasb.fxgl.dsl.components.ProjectileComponent; // Import needed
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent; // Import needed (Corrected path)
import javafx.geometry.Point2D; // Import needed
import javafx.scene.Group; // Import needed
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // Added import
import javafx.scene.paint.Color; // Import needed
import javafx.scene.shape.Circle; // Import needed
import javafx.util.Duration; // Import needed
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("ship")
    public Entity newShip(SpawnData data) {
        Image shipImage = data.get("shipImage"); // Get image from SpawnData

        return entityBuilder(data)
                .type(EntityType.SHIP)
                .viewWithBBox(new ImageView(shipImage)) // Use ImageView with the passed image
                .with(new CollidableComponent(true))
                .with(new ShipComponent())
                .build();
    }

    @Spawns("asteroid")
    public Entity newAsteroid(SpawnData data) {
        Image asteroidImage = data.get("asteroidImage");

        return entityBuilder(data)
                .type(EntityType.ASTEROID)
                .viewWithBBox(new ImageView(asteroidImage)) // Use ImageView instead of texture()
                .with(new CollidableComponent(true))
                .with(new AsteroidComponent())
                .build();
    }

    // --- Add this method ---
    // --- Add this method --- // This comment is slightly inaccurate now, it's being modified
    @Spawns("laser")
    public Entity newLaser(SpawnData data) {
        // Define appearance: a glowing yellow circle
        var radius = 5; // Set the radius of the circle
        var circle = new Circle(radius, Color.YELLOW); // Create the circle

        // Apply a glow effect using DropShadow
        var glow = new javafx.scene.effect.DropShadow();
        glow.setColor(Color.YELLOW); // Glow color
        glow.setRadius(15);         // How far the glow extends
        glow.setSpread(0.5);        // Density of the glow
        circle.setEffect(glow);     // Apply the effect

        return entityBuilder(data)
                .type(EntityType.LASER)
                .viewWithBBox(circle) // Use the circle as the view and bounding box
                .with(new ProjectileComponent(new Point2D(0, -1), 400)) // Move straight up (0, -1) at speed 400
                .with(new OffscreenCleanComponent()) // Remove when offscreen
                .with(new CollidableComponent(true)) // Laser can now collide
                .build();
    }
    // --- End of added method --- // Comment still slightly inaccurate

    // Removed asteroidExplosionEffect spawner - will use particle emitter instead

    @Spawns("shapeExplosionEffect")
    public Entity newShapeExplosion(SpawnData data) {
        var duration = Duration.seconds(0.3); // Duration of the effect

        // Create multiple circles for the burst effect
        var explosionGroup = new Group();
        int numCircles = 8;
        double radius = 3;
        Color color = Color.ORANGERED;

        for (int i = 0; i < numCircles; i++) {
            Circle circle = new Circle(radius, color);
            // Position circles slightly offset from center initially
            double angle = 2 * Math.PI * i / numCircles;
            circle.setTranslateX(Math.cos(angle) * radius * 2);
            circle.setTranslateY(Math.sin(angle) * radius * 2);
            explosionGroup.getChildren().add(circle);
        }

        return entityBuilder(data)
                .view(explosionGroup) // Use the group of circles as the view
                .with(new ExplosionAnimationComponent(duration)) // Add the animation component
                .with(new ExpireCleanComponent(duration)) // Remove after animation
                .build();
    }
}
