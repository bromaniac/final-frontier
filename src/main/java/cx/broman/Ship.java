package cx.broman;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;


public class Ship {
    private final Image image;
    private final int y;
    private int x;
    private boolean isShooting;

    public Ship(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isShooting = false;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, image.getWidth(), image.getHeight());
    }

    public void moveLeft(int minX) {
        x = Math.max(minX, x - 5);
    }

    public void moveRight(int maxX) {
        x = Math.min(maxX - 20, x + 5);
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

} 