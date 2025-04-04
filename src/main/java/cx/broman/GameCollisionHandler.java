package cx.broman;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

public class GameCollisionHandler extends com.almasb.fxgl.physics.CollisionHandler {

    public GameCollisionHandler(EntityType a, EntityType b) {
        super(a, b);
    }

    @Override
    protected void onCollisionBegin(Entity a, Entity b) {
        // Implement collision logic here
    }
}
