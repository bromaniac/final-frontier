package cx.broman;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
// Removed particle imports as they cause compilation errors
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.texture.Texture; // Added import for Texture
import javafx.geometry.Point2D; // Import needed
// Removed unused Interpolator import
import javafx.scene.image.Image; // Added import
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
// Removed unused Duration import
import static com.almasb.fxgl.dsl.FXGL.*; // Added static import for DSL

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FinalFrontier extends GameApplication {

    private Entity ship;
    private final List<Entity> asteroids = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    // private int groundY = -200; // Removed unused variable
    private boolean gameOver = false;
    private long explosionStartTime = 0;
    private static final long EXPLOSION_DURATION = 1_000_000_000L; // 1 second in nanoseconds
    private boolean showExplosion = false; // For game over explosion
    private double explosionX, explosionY; // For game over explosion


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Final Frontier");
        settings.setWidth(300);
        settings.setHeight(400);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new GameUserAction("Move Left") { // Renamed UserAction
            @Override
            protected void onAction() {
                if (ship != null) {
                    ship.getComponent(ShipComponent.class).moveLeft();
                }
            }
        }, KeyCode.LEFT);

        input.addAction(new GameUserAction("Move Right") { // Renamed UserAction
            @Override
            protected void onAction() {
                if (ship != null) {
                    ship.getComponent(ShipComponent.class).moveRight();
                }
            }
        }, KeyCode.RIGHT);

        input.addAction(new GameUserAction("Shoot") { // Renamed UserAction
            @Override
            protected void onActionBegin() {
                if (ship != null) {
                    ship.getComponent(ShipComponent.class).shoot();
                }
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());

        // Create two scrolling background entities for seamless looping
        Texture backgroundTexture = texture("cx/broman/mark.gif"); // Load the texture once

        // Entity 1 - Initially visible
        entityBuilder()
                .at(0, 0)
                .zIndex(-1) // Draw behind everything else
                .view(backgroundTexture.copy()) // Use a copy for the view
                .with(new BackgroundScrollComponent())
                .buildAndAttach();

        // Entity 2 - Positioned directly above Entity 1, initially off-screen
        entityBuilder()
                .at(0, -backgroundTexture.getHeight()) // Position above the first one
                .zIndex(-1)
                .view(backgroundTexture.copy()) // Use another copy
                .with(new BackgroundScrollComponent())
                .buildAndAttach();


        // Create ship and load its image
        ship = spawn("ship", new SpawnData(140, 350).put("shipImage", image("cx/broman/skepp.gif"))); // Corrected path

        // Initialize asteroids
        int maxAsteroids = 3;
        for (int i = 0; i < maxAsteroids; i++) {
            Image asteroidImage = image("cx/broman/aster" + i + ".gif"); // Corrected path
            // SpawnData no longer needs initial position (0,0) as resetPosition handles it
            Entity asteroid = spawn("asteroid", new SpawnData().put("asteroidImage", asteroidImage));
            // Explicitly call resetPosition for initial placement logic
            asteroid.getComponent(AsteroidComponent.class).resetPosition();
            asteroids.add(asteroid);
        }

        // Removed manual background scrolling timer
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.addCollisionHandler(new GameCollisionHandler(EntityType.SHIP, EntityType.ASTEROID) { // Renamed CollisionHandler
            @Override
            protected void onCollisionBegin(Entity ship, Entity asteroid) {
                gameOver = true;
                showExplosion = true;
                explosionStartTime = 0;
                explosionX = ship.getX() + ship.getWidth() / 2;
                explosionY = ship.getY() + ship.getHeight() / 2;
                createExplosion(explosionX, explosionY);
                // Remove ship and asteroid after collision? Maybe just game over?
                // ship.removeFromWorld(); // Example if needed
                // asteroid.removeFromWorld(); // Example if needed
            }
        });

        // Add collision handler for LASER vs ASTEROID using anonymous class
        physics.addCollisionHandler(new GameCollisionHandler(EntityType.LASER, EntityType.ASTEROID) {
            @Override
            protected void onCollisionBegin(Entity laser, Entity asteroid) {
                // Get asteroid position for explosion center
                double explosionX = asteroid.getX() + asteroid.getWidth() / 2;
                double explosionY = asteroid.getY() + asteroid.getHeight() / 2;

                // Remove both entities
                laser.removeFromWorld();
                asteroid.removeFromWorld();
                asteroids.remove(asteroid); // Also remove from the tracking list

                // Spawn the shape-based explosion effect
                spawn("shapeExplosionEffect", new SpawnData(explosionX, explosionY));

                // Spawn a new asteroid to replace the destroyed one
                Image newAsteroidImage = image("cx/broman/aster" + random.nextInt(3) + ".gif"); // Random image
                Entity newAsteroid = spawn("asteroid", new SpawnData().put("asteroidImage", newAsteroidImage)); // Use default SpawnData
                // Reset its position using the component's logic (which places it randomly at the top)
                newAsteroid.getComponent(AsteroidComponent.class).resetPosition();
                asteroids.add(newAsteroid); // Add the new asteroid to the tracking list
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        if (!gameOver) {
            // Check and update asteroids
            checkAsteroids();
            moveAsteroids();


            // Update manual particles (if still needed for game over)
            updateParticles();
        } else {
            // Game Over logic
            if (showExplosion) {
                // If this is the first frame of explosion
                if (explosionStartTime == 0) {
                    explosionStartTime = System.nanoTime();
                }
                // Show explosion for EXPLOSION_DURATION
                if (System.nanoTime() - explosionStartTime < EXPLOSION_DURATION) {
                    // Keep rendering the game state with explosion
                } else {
                    showExplosion = false;
                    renderGameOver();
                }
            } else {
                renderGameOver();
            }
        }
    }

    private void checkAsteroids() {
        asteroids.forEach(asteroid -> {
            if (asteroid.getY() > getAppHeight()) {
                asteroid.getComponent(AsteroidComponent.class).resetPosition();
            }
        });
    }

    private void moveAsteroids() {
        asteroids.forEach(asteroid -> asteroid.getComponent(AsteroidComponent.class).move());
    }

    private void createExplosion(double x, double y) {
        int particleCount = 50; // Increased number of particles
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = random.nextDouble() * 3 + 2; // Increased speed
            double life = random.nextDouble() * 0.8 + 0.7; // Longer life
            double size = random.nextDouble() * 4 + 2; // Varying sizes

            // Create a more dynamic color palette
            Color color;
            if (random.nextDouble() < 0.6) {
                // Main explosion color (orange-red)
                color = Color.rgb(
                        255,
                        (int) (random.nextDouble() * 100 + 50),
                        0,
                        random.nextDouble() * 0.7 + 0.3
                );
            } else if (random.nextDouble() < 0.8) {
                // Yellow-white core
                color = Color.rgb(
                        255,
                        255,
                        (int) (random.nextDouble() * 100 + 150),
                        random.nextDouble() * 0.7 + 0.3
                );
            } else {
                // Red outer particles
                color = Color.rgb(
                        255,
                        0,
                        0,
                        random.nextDouble() * 0.5 + 0.2
                );
            }

            particles.add(new Particle(
                    x,
                    y,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    life,
                    color,
                    size
            ));
        }
    }

    private void updateParticles() {
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) {
            p.update();
        }
    }

    private void renderGameOver() {
        // Display "Game Over" message
        getGameScene().clearGameViews();
        getGameScene().addUINode(getUIFactoryService().newText("GAME OVER", Color.RED, 32));
    }
}
