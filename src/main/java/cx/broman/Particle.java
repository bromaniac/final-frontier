package cx.broman;

import javafx.scene.paint.Color;

public class Particle {
    public double x, y, velX, velY, life, maxLife, size, rotation;
    public Color color;

    public Particle(double x, double y, double velX, double velY, double life, Color color, double size) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.life = life;
        this.maxLife = life;
        this.color = color;
        this.size = size;
        this.rotation = Math.random() * 360;
    }

    public void update() {
        x += velX;
        y += velY;
        life -= 0.016; // Assuming 60 FPS
        rotation += 1;
    }

    public boolean isDead() {
        return life <= 0;
    }
}
