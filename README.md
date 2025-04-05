# Final Frontier
A simple space-themed game built with the **FXGL game engine** where players control a spaceship to avoid asteroids.

A long time ago (2000! 25 years ago) I started creating this stupid "game" as an Applet to learn Java
using JDK 1.1 LOL. The original code is available in FinalFrontier.original.

I decided to use it as a starting point to play around with LLMs to learn more
about them.

This is a work in progress...

## Game Description
Final Frontier is a 2D arcade-style game built using the FXGL engine. The player controls a spaceship navigating through a field of asteroids. The goal is to survive as long as possible by avoiding collisions. The game utilizes FXGL's features for:
- Entity-component system
- Input handling
- Physics and collision detection (ship vs. asteroids)
- Seamless background scrolling
- Explosion animations for ship and asteroid destruction
- Game over state upon ship destruction

## Project Structure (FXGL)
The game leverages the FXGL engine and its entity-component system:
- `FinalFrontier`: Main `GameApplication` class handling settings, input, game initialization, physics, and the update loop.
- **Components:** Reusable logic attached to entities.
    - `ShipComponent`: Manages ship movement and shooting actions.
    - `AsteroidComponent`: Controls asteroid movement and reset logic.
    - `BackgroundScrollComponent`: Implements the seamless vertical scrolling of the background texture.
    - `ExplosionAnimationComponent`: Handles the display of explosion animations.
- **Factories & Handlers:**
    - `GameEntityFactory`: Defines how to spawn game entities (ship, asteroids, background).
    - `GameCollisionHandler`: Manages collision logic between the ship and asteroids.
- **Other Key Classes:**
    - `EntityType`: Enum defining types for collision filtering.
    - `GameUserAction`: Custom input actions for player controls.
    - `Particle`: Represents individual particles for explosion effects (managed within `FinalFrontier`).

## Requirements
- Java JDK 24 or higher
- **FXGL game engine** (version compatible with the project - typically managed by Maven)

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
- **Space**: Shoot

## Development (FXGL Structure)
This project uses the FXGL engine and follows a standard Maven project layout. Key game logic is implemented using FXGL's entity-component pattern:
```
src/
├── main/
│   ├── java/
│   │   └── cx/
│   │       └── broman/
│   │           ├── FinalFrontier.java          # Main GameApplication
│   │           ├── EntityType.java           # Entity type definitions
│   │           ├── GameCollisionHandler.java # Collision logic
│   │           ├── GameEntityFactory.java    # Spawning entities
│   │           ├── GameUserAction.java       # Input handling actions
│   │           ├── Particle.java             # Explosion particle logic
│   │           ├── components/               # (Conceptual - components are in broman/)
│   │           │   ├── AsteroidComponent.java
│   │           │   ├── BackgroundScrollComponent.java
│   │           │   ├── ExplosionAnimationComponent.java
│   │           │   └── ShipComponent.java
│   │           └── ... (potentially other helper classes)
│   └── resources/
│       └── assets/                           # FXGL asset structure
│           └── textures/
│               └── cx/broman/
│                   └── [game textures .gif, .png]
```
The core game loop, rendering, and physics are managed by FXGL. Custom game logic is primarily implemented within Components, Factories, and Handlers.
## License
Apache 2.0
## Credits
Created vibe coding with different tools including Cline, Windsurf, Cursor and IntelliJ.
