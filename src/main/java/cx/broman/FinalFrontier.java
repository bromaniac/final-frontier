package cx.broman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FinalFrontier extends Application {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private Canvas canvas;
    private GraphicsContext gc;
    private Image mark;
    private Ship ship;
    private int groundY = -200;
    private int xMax, yMax;
    private Asteroid[] asteroids;
    private AnimationTimer gameLoop;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the game window
        StackPane root = new StackPane();
        canvas = new Canvas(300, 400);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Final Frontier");

        initGame();
        setupInputHandling(scene);
        startGameLoop();

        primaryStage.show();
    }

    private void initGame() {
        xMax = (int) canvas.getWidth() - 1;
        yMax = (int) canvas.getHeight() - 1;

        // Load images
        try {
            String baseUrl = Objects.requireNonNull(getClass().getResource("")).toExternalForm();
            mark = new Image(baseUrl + "mark.gif");
            Image ship = new Image(baseUrl + "skepp.gif");
            this.ship = new Ship(ship, 140, 350);

            // Initialize asteroids
            int maxAsteroids = 3;
            asteroids = new Asteroid[maxAsteroids];
            for (int i = 0; i < maxAsteroids; i++) {
                Image asteroidImage = new Image(baseUrl + "aster" + i + ".gif");
                asteroids[i] = new Asteroid(asteroidImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupInputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    private void startGameLoop() {
        // Create and store the animation timer
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                render();
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        // Update ship position based on pressed keys
        if (pressedKeys.contains(KeyCode.LEFT)) {
            ship.moveLeft(0);
        }
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            ship.moveRight(xMax);
        }

        ship.setShooting(pressedKeys.contains(KeyCode.SPACE));

        // Update ground position (scrolling background)
        groundY = (groundY + 2) % 200;

        // Check and update asteroids
        checkAsteroids();
        moveAsteroids();

        // Check for collisions
        checkCollision();   // Call the new collision detection logic

        // Draw laser
        if (ship.isShooting()) {
            gc.setFill(javafx.scene.paint.Color.GREEN);
            gc.fillRect(ship.getX() + 18, ship.getY(), 2, -ship.getY());
            // Check for collision with asteroids
            for (Asteroid asteroid : asteroids) {
                if (asteroid.checkCollision(ship.getX())) {
                    asteroid.resetPosition();
                }
            }
        }
    }

    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw scrolling background
        for (int y = groundY; y < yMax; y += 200) {
            gc.drawImage(mark, 0, y);
        }

        // Draw spaceship
        gc.drawImage(ship.getImage(), ship.getX(), ship.getY());

        // Draw asteroids
        for (Asteroid asteroid : asteroids) {
            gc.drawImage(asteroid.getImage(), asteroid.getX(), asteroid.getY());
        }
    }

    private void checkAsteroids() {
        for (Asteroid asteroid : asteroids) {
            if (asteroid.isOutOfBounds(yMax)) {
                asteroid.resetPosition();
            }
        }
    }

    private void moveAsteroids() {
        for (Asteroid asteroid : asteroids) {
            asteroid.move();
        }
    }

    private void checkCollision() {
        for (Asteroid asteroid : asteroids) {
            if (asteroid == null) continue; // Skip if the asteroid doesn't exist
            Rectangle2D asteroidBounds = asteroid.getBounds();
            Rectangle2D shipBounds = ship.getBounds();

            if (shipBounds.intersects(asteroidBounds)) {
                gameOver();
                break; // Exit loop after game over
            }
        }
    }

    private void gameOver() {
        // Stop the game loop
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Display "Game Over" message
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.setFont(new javafx.scene.text.Font("Arial", 48));
        gc.fillText("GAME OVER", (double) xMax / 2 - 120, (double) yMax / 2);

        System.out.println("Game Over - Ship collided with asteroid");
    }
}
