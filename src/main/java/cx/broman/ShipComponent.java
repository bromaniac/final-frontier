package cx.broman;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ShipComponent extends Component {

    private final double speed = 100;
    private boolean shooting = false;

    public void moveLeft() {
        double moveAmount = speed * tpf();
        if (entity.getX() - moveAmount >= 0) {
            entity.translateX(-moveAmount);
        }
    }

    public void moveRight() {
        if (entity != null) {
            double moveAmount = speed * tpf();
            // Use getAppWidth() for the right boundary check
            if (entity.getX() + entity.getWidth() + moveAmount <= getAppWidth()) {
                entity.translateX(moveAmount);
            }
        }
    }

    public void shoot() {
        if (!shooting) {
            shooting = true;
            runOnce(() -> shooting = false, Duration.seconds(0.5));

            spawn("laser", new SpawnData(entity.getX() + 18, entity.getY()));
        }
    }
}
