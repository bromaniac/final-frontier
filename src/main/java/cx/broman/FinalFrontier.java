package cx.broman;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
// Removed particle imports as they cause compilation errors
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.texture.Texture; // Added import for Texture
// Removed unused Interpolator import
import javafx.scene.image.Image; // Added import
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration; // Re-added needed import
import static com.almasb.fxgl.dsl.FXGL.*; // Added static import for DSL

import java.util.ArrayList;
import java.util.Iterator; // Import needed
import java.util.List;
import java.util.Random;

public class FinalFrontier extends GameApplication {

    private Entity ship;
    private final List<Entity> asteroids = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    // private int groundY = -200; // Removed unused variable
    private boolean gameOver = false;
    // Removed explosionStartTime, EXPLOSION_DURATION
    private boolean isGameOverScreenPending = false; // Flag to delay game over screen
    private boolean isGameOverScreenRendered = false; // Flag to ensure renderGameOver runs only once


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
                if (gameOver || ship == null) return; // Check game over state
                ship.getComponent(ShipComponent.class).moveLeft();
            }
        }, KeyCode.LEFT);

        input.addAction(new GameUserAction("Move Right") { // Renamed UserAction
            @Override
            protected void onAction() {
                if (gameOver || ship == null) return; // Check game over state
                ship.getComponent(ShipComponent.class).moveRight();
            }
        }, KeyCode.RIGHT);

        input.addAction(new GameUserAction("Shoot") { // Renamed UserAction
            @Override
            protected void onActionBegin() {
                if (gameOver || ship == null) return; // Check game over state
                ship.getComponent(ShipComponent.class).shoot();
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGame() {
        // Reset game state flags
        gameOver = false;
        isGameOverScreenPending = false;
        isGameOverScreenRendered = false;
        asteroids.clear(); // Clear the list before adding new ones
        // Note: Existing entities are cleared by FXGL's startNewGame -> initGame sequence

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
        physics.addCollisionHandler(new GameCollisionHandler(EntityType.SHIP, EntityType.ASTEROID) {
            @Override
            protected void onCollisionBegin(Entity ship, Entity asteroid) {
                if (gameOver) return; // Prevent multiple triggers if already game over

                gameOver = true;

                // Calculate explosion position before removing ship
                double shipExplosionX = ship.getX() + ship.getWidth() / 2;
                double shipExplosionY = ship.getY() + ship.getHeight() / 2;

                // Spawn the ship explosion effect
                spawn("shipExplosionEffect", new SpawnData(shipExplosionX, shipExplosionY));

                // Remove ship and asteroid
                ship.removeFromWorld();
                asteroid.removeFromWorld();
                asteroids.remove(asteroid); // Remove from tracking list too

                // Use runOnce to delay showing the game over screen after the explosion animation
                runOnce(() -> {
                    isGameOverScreenPending = true;
                }, Duration.seconds(0.6)); // Delay slightly longer than ship explosion (0.5s)
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
        // Always move asteroids
        moveAsteroids();

        if (!gameOver) {
            // Check asteroids only if game is running
            checkAsteroids();

            // Update manual particles (if still needed for game over - currently not used)
            updateParticles();
        } else {
            // Game Over logic: Wait for the delay timer and ensure screen is rendered only once
            if (isGameOverScreenPending && !isGameOverScreenRendered) {
                renderGameOver();
                isGameOverScreenRendered = true; // Mark as rendered
            }
            // Otherwise, do nothing, allowing explosion animation or just waiting
        }
    }

    private void checkAsteroids() {
        // Use iterator to allow safe removal while iterating
        for (Iterator<Entity> it = asteroids.iterator(); it.hasNext(); ) {
            Entity asteroid = it.next();
            if (asteroid.getY() > getAppHeight()) {
                if (!gameOver) {
                    // If game is running, reset position
                    asteroid.getComponent(AsteroidComponent.class).resetPosition();
                } else {
                    // If game is over, remove the asteroid completely
                    asteroid.removeFromWorld();
                    it.remove(); // Remove from the list using iterator
                }
            }
        }
    }

    private void moveAsteroids() {
        asteroids.forEach(asteroid -> asteroid.getComponent(AsteroidComponent.class).move());
    }

    private void updateParticles() {
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) {
            p.update();
        }
    }

    private void renderGameOver() {
        // Don't clear the whole view, just add UI elements on top
        // getGameScene().clearGameViews();

        // "GAME OVER" Text
        var gameOverText = getUIFactoryService().newText("GAME OVER", Color.RED, 32);
        // Manually position the text near top-center
        // Estimate text width for centering (adjust as needed)
        double estimatedTextWidth = 200; // Adjust based on font size and text length
        gameOverText.setTranslateX((getAppWidth() - estimatedTextWidth) / 2.0);
        gameOverText.setTranslateY(getAppHeight() / 3.0); // Position 1/3 down

        // "Play Again" Button
        var playAgainButton = getUIFactoryService().newButton("Play Again");
        playAgainButton.setOnAction(e -> getGameController().startNewGame());

        // Manually position the button near bottom-center
        // Estimate button width for centering (adjust as needed)
        double estimatedButtonWidth = 100;
        playAgainButton.setTranslateX((getAppWidth() - estimatedButtonWidth) / 2.0);
        playAgainButton.setTranslateY(getAppHeight() * 0.8); // Position 80% down

        getGameScene().addUINodes(gameOverText, playAgainButton);
    }
}
