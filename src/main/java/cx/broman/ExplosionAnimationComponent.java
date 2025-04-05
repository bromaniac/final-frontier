package cx.broman;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

/**
 * Animates the shapes within an entity's view to create an explosion effect
 * (scaling up and fading out). Assumes the view is a Group containing shapes.
 */
public class ExplosionAnimationComponent extends Component {

    private Duration duration;

    public ExplosionAnimationComponent(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void onAdded() {
        ViewComponent viewComponent = entity.getViewComponent();

        // Get all nodes (shapes) within the view
        List<Node> shapes = viewComponent.getChildren();

        // Animate each shape
        for (Node shape : shapes) {
            FXGL.animationBuilder()
                    .duration(duration)
                    .interpolator(Interpolator.EASE_OUT)
                    .scale(shape)
                    .from(new javafx.geometry.Point2D(1, 1))
                    .to(new javafx.geometry.Point2D(3, 3)) // Scale up 3x
                    .buildAndPlay();

            FXGL.animationBuilder()
                    .duration(duration)
                    .interpolator(Interpolator.EASE_OUT)
                    .fadeOut(shape) // Fade out
                    .buildAndPlay();
        }
    }
}
