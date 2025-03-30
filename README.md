# Final Frontier
A simple space-themed game built with JavaFX where players control a spaceship to avoid asteroids.

A long time ago (2000! 25 years ago) I started creating this stupid "game" as an Applet to learn Java
using JDK 1.1 LOL. I decided to use it as a starting point to play around with LLMs to learn more 
about them.

This is a work in progress...

## Game Description
Final Frontier is a 2D arcade-style game where the player controls a spaceship navigating through a field of asteroids. The goal is to survive as long as possible by avoiding collisions with asteroids. The game features:
- Spaceship movement (left/right controls)
- Randomly generated asteroids
- Collision detection
- Particle effects for explosions
- Game over screen with restart functionality

## Project Structure
The game is organized into the following main classes:
- `FinalFrontier`: Main application class that handles game initialization, rendering, and game loop
- `Ship`: Represents the player's ship with movement and collision detection
- `Asteroid`: Represents the asteroids with movement patterns and collision detection
- `Particle`: Manages explosion particle effects (inferred from the code)

## Requirements
- Java JDK 24 or higher
- JavaFX

## Building the Project
This project uses Maven as its build system. To build the project:
``` bash
mvn clean package
```
This will create an executable JAR file in the `target` directory.
## Running the Game
### Method 1: Using Maven
``` bash
mvn javafx:run
```
### Method 2: Running the JAR directly
``` bash
java -jar target/final-frontier-1.0.jar
```
## Game Controls
- **Left Arrow**: Move ship left
- **Right Arrow**: Move ship right
- **Space**: Shoot (TODO)
- **R**: Restart the game after game over

## Development
This project is a simple JavaFX application structured in a standard Maven project layout:
``` 
src/
├── main/
│   ├── java/
│   │   └── cx/
│   │       └── broman/
│   │           ├── FinalFrontier.java
│   │           ├── Ship.java
│   │           ├── Asteroid.java
│   │           └── Particle.java
│   └── resources/
│       └── [game assets]
```
## License
Apache 2.0
## Credits
Created vibe coding with different tools including Windsurf, Cursor and IntelliJ.
