package cx.broman;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // Added import

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
}
