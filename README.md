# Counter-Strike 2 GSI

A Java library to interface with the **Game State Integration (GSI)** found in Counter-Strike 2.

Counter-Strike 2 can be configured to send real-time match data (players, weapons, bomb state,
grenades, rounds, kills, and more) as HTTP POST requests to a local endpoint. This library starts a
listener for those requests, parses the JSON into strongly-typed objects, and dispatches granular,
subscribable game events to your application.

## Features

- **Simple listener** – start an HTTP server with a single class and subscribe to what you need.
- **Automatic config generation** – generates the required `gamestate_integration_*.cfg` file for you.
- **Typed game state** – raw GSI JSON is parsed into a rich `GameState` model.
- **Granular events** – dozens of event types such as `PlayerGotKill`, `PlayerDied`, `KillFeed`,
  `BombStateUpdated`, `RoundStarted`, `RoundConcluded`, grenade events, and more.
- **JavaFX event viewer** – an included GUI to inspect events live.

## Requirements

- **Java 17** or newer
- **Maven 3.6+**
- **Counter-Strike 2** (to actually emit game state)

## Project structure

This is a multi-module Maven project:

| Module    | Artifact         | Description                                                           |
| --------- | ---------------- | -------------------------------------------------------------------- |
| `lib`     | `cs2gsi`         | The core library that listens for and parses GSI events.             |
| `example` | `cs2gsi-example` | A runnable console program demonstrating common event subscriptions. |
| `viewer`  | `cs2gsi-viewer`  | A JavaFX UI for live-viewing selected GSI events.                    |

## Building

Build all modules from the repository root:

```bash
mvn clean install
```

## Usage

Add the library as a dependency (after building/installing it locally):

```xml
<dependency>
    <groupId>com.cs2gsi</groupId>
    <artifactId>cs2gsi</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then start a listener and subscribe to events:

```java
import com.cs2gsi.GameStateListener;
import com.cs2gsi.events.PlayerGotKill;
import com.cs2gsi.events.RoundStarted;

public class Main {
    public static void main(String[] args) throws Exception {
        try (GameStateListener gsl = new GameStateListener(4000)) {
            // Generate the gamestate_integration_*.cfg file CS2 needs.
            gsl.generateGSIConfigFile("Example");

            // React to the entire game state on every update.
            gsl.onNewGameState(state -> {
                // inspect state ...
            });

            // Or subscribe to specific, granular events.
            gsl.subscribe(PlayerGotKill.class, e ->
                System.out.println(e.player.name + " got a kill with " + e.weapon.name + "!"));

            gsl.subscribe(RoundStarted.class, e ->
                System.out.println("Round " + e.round + " started."));

            if (!gsl.start()) {
                System.out.println("Could not start. Try running as Administrator.");
                return;
            }

            System.out.println("Listening for game integration calls... Press ENTER to quit.");
            System.in.read();
        }
    }
}
```

See [`example/src/main/java/com/cs2gsi/example/Program.java`](example/src/main/java/com/cs2gsi/example/Program.java)
for a fuller demonstration.

### Running the example

```bash
mvn -pl example -am clean package
java -jar example/target/cs2gsi-example-1.0.0.jar
```

> On Windows, binding the listener may require running as **Administrator**.

### Running the JavaFX viewer

Install the modules first, then invoke the `javafx:run` goal on the viewer module alone
(plugin goals, unlike lifecycle phases, cannot be combined with `-am`):

```bash
mvn clean install
mvn -pl viewer javafx:run
```

## Configuring Counter-Strike 2

`generateGSIConfigFile(...)` writes a `gamestate_integration_*.cfg` file into the game's
`csgo/cfg` directory, pointing CS2 at your listener (e.g. `http://127.0.0.1:4000/`). Restart
Counter-Strike 2 after the file is generated so it picks up the integration. The port passed to
`GameStateListener` must match the one in the generated config.
