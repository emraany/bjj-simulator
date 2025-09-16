# BJJ Simulator

A Brazilian Jiu-Jitsu progression simulator written in **Java 21**, designed as an OOP-heavy project that models moves, positions, transitions, and outcomes in a realistic way.  
This project demonstrates strong object-oriented design, JSON-driven catalogs, and simulation logic — built to showcase technical depth for internships and portfolio use.


## Features

- **19 Core Positions**: Standing, Closed Guard, Open Guard, Half Guard, Side Control, Mount, Back Control, Turtle, Knee-on-Belly, and North-South.  
- **Move Catalog in JSON**: Entries, sweeps, passes, and submissions are defined in `resources/catalog/moves.json`.  
- **Simulation Engine**:
  - Each move has a probability model with success, partial, and failure outcomes, giving realistic variability instead of fixed transitions.  
  - Position states update dynamically after every move, ensuring continuity across sweeps, passes, and submissions.  
  - Submissions act as terminal events — if successful, the simulation ends immediately with a tap outcome, mirroring real BJJ match flow.  
  - Resistance level, skill ratings, and fatigue settings all influence probabilities, allowing the simulation to scale from easy rolling to hard competition scenarios.  
  - Deterministic seeding makes runs reproducible, while leaving it blank creates organic randomness for variety.  

- **CLI Interaction**:
  - Choose starting position
  - Configure resistance (0–100)
  - Optional skill/fatigue settings
  - Deterministic runs with seed values  

## Project Structure
src/main/java/com/bjj/simulator/ → core simulation logic  
src/test/java/com/bjj/simulator/ → unit tests  
resources/catalog/moves.json → move definitions  

## Running the Simulator

### Prerequisites
- Java 21+  
- Maven 3.9+  

### Run
```bash
mvn -q exec:java
```
## Next Up

Planned extensions to the simulator:

- **Spring Boot API**: Expose the simulation engine over REST endpoints to support web and mobile clients.  
- **JavaFX UI**: Visual interface to explore positions and transitions instead of CLI-only interaction.  
- **Expanded Move Catalog**: Add more passes, sweeps, and submissions for greater realism.  
- **Analytics Dashboard**: Export results to JSON/CSV and visualize stats (e.g., most common transitions, average session length).  
