package cx.broman;

import javafx.scene.image.Image;

public class Ship {
    private Image image;
    private int x;
    private final int y;
    private boolean isShooting;

    public Ship(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isShooting = false;
    }

    public void moveLeft(int minX) {
        x = Math.max(minX, x - 5);
    }

    public void moveRight(int maxX) {
        x = Math.min(maxX - 20, x + 5);
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public boolean isShooting() {
        return isShooting;
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