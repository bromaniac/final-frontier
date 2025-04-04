package cx.broman;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ShipComponent extends Component {

    private double speed = 100;
    private boolean shooting = false;

    public void moveLeft() {
        entity.translateX(-speed * tpf());
    }

    public void moveRight() {
        if (entity != null) {
            entity.translateX(speed * tpf());
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
