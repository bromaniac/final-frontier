package cx.broman;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class Asteroid {
    private final Image image;
    private final int dy;
    private int x;
    private int y;

    public Asteroid(Image image) {
        this.image = image;
        this.dy = 1 + (int) (Math.random() * 3);
        resetPosition();
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, image.getWidth(), image.getHeight());
    }

    public void resetPosition() {
        this.x = (int) (Math.random() * 250.0);
        this.y = (int) (Math.random() * 400.0 - 650.0);
    }

    public void move() {
        y += dy;
    }

    public boolean isOutOfBounds(int maxY) {
        return y > maxY;
    }

    public boolean checkCollision(int shipX) {
        return shipX - x > -5 && shipX - x < 5;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

} 