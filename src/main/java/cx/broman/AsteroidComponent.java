package cx.broman;

import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

public class AsteroidComponent extends Component {

    public void move() {
        double speed = 50;
        entity.translateY(speed * tpf());
    }

    public void resetPosition() {
        entity.setPosition(Math.random() * getAppWidth(), -50);
    }
}
