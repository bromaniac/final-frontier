package cx.broman;

import javafx.scene.paint.Color;

public class Particle {
    double x, y;
    double vx, vy;
    double life;
    double maxLife;
    Color color;
    double size;
    double rotation;
    double rotationSpeed;

    public Particle(double x, double y, double vx, double vy, double life, Color color, double size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.maxLife = life;
        this.color = color;
        this.size = size;
        this.rotation = Math.random() * 360;
        this.rotationSpeed = (Math.random() - 0.5) * 10;
    }

    public void update() {
        x += vx;
        y += vy;
        // Add some gravity effect
        vy += 0.1;
        // Slow down particles over time
        vx *= 0.98;
        vy *= 0.98;
        life -= 0.016; // Assuming 60 FPS
        rotation += rotationSpeed;
    }

    public boolean isDead() {
        return life <= 0;
    }
} 