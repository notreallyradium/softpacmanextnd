**Pac-Man Game**
-------
Overview

- This project is an extended version of Pac-Man, implementing unique ghost behaviors and game mechanics using various design patterns. The codebase is designed with flexibility and maintainability in mind, allowing for easy modifications and future extensions.

------

How to Run

To execute the game, use the following command in the terminal:

**gradle clean build run**

------
**Design Patterns Implemented**

This project incorporates the following design patterns to manage complexity and improve maintainability:

1, Strategy Pattern (for Ghost Chase Behaviors)

Purpose: Allows each ghost to have a unique chase behavior based on its type (Blinky, Pinky, Inky, Clyde).

Classes Involved:

- GhostImpl (context that uses a ChaseBehavior strategy)
- ChaseBehavior (interface defining the calculateTarget() method)
- BlinkyChase, PinkyChase, InkyChase, ClydeChase (concrete strategy classes implementing ChaseBehavior)

How it Works: GhostImpl selects a chase behavior through ChaseBehavior, enabling each ghost type to follow its unique chase logic without modifying core ghost functionality.


2, Observer Pattern (for Player Position Updates)

Purpose: Keeps ghosts updated with the player’s position, essential for accurate chase behaviors.

Classes Involved:

- PlayerPositionObserver (interface for observer classes)
- GhostImpl (implements PlayerPositionObserver to receive position updates)
- LevelImpl (subject, notifying ghosts of player movements)

How it Works: LevelImpl regularly updates registered ghosts on the player’s current position, allowing real-time target adjustments.


3, Factory Pattern (for Entity Creation)

Purpose: Manages the creation of various game entities, such as Ghosts, Pellets, and Power Pellets, in a centralized and flexible manner.

Classes Involved:

- RenderableFactory (interface for factories)
- GhostFactory, PelletFactory, PowerPelletFactory (concrete factory classes for specific entities)
- RenderableFactoryRegistry (registry that holds and provides access to entity factories)

How it Works: Each entity factory is responsible for creating instances of specific game entities. RenderableFactoryRegistry consolidates these factories, simplifying the entity creation process.

----
**Additional Information**

Configuration

- Game Settings: Configured in config.json within src/main/resources. Modify parameters like ghost speeds, level details, and pellet scores as needed.
- Map Layout: Defined in map.txt. Changing this file adjusts the game’s layout and entity placements.

Extensibility

This project follows SOLID principles, particularly the Open-Closed Principle, to facilitate extension without modifying existing code. New ghost types or behaviors can be added by implementing existing interfaces or extending base classes.

-----
Known Issues

- High-frequency updates through the Observer Pattern may lead to performance lags. Future optimizations could involve adjusting update frequencies or batching updates to improve efficiency and gameplay smoothness.
