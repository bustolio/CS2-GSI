[![](https://jitpack.io/v/bustolio/CS2-GSI.svg)](https://jitpack.io/#bustolio/CS2-GSI)

# Counter-Strike 2 GSI

A Java library to interface with the **Game State Integration (GSI)** found in Counter-Strike 2.

Counter-Strike 2 can be configured to send real-time match data (players, weapons, bomb state,
grenades, rounds, kills, and more) as HTTP POST requests to a local endpoint. This library starts a
listener for those requests, parses the JSON into strongly-typed objects, and dispatches granular,
subscribable game events to your application.

## Features

- **Simple listener.** Start an HTTP server with a single class and subscribe to what you need.
- **Automatic config generation.** The library writes the `gamestate_integration_*.cfg` file for you.
- **Typed game state.** The library parses the raw GSI JSON into a `GameState` model.
- **Granular events.** Dozens of event types such as `PlayerGotKill`, `PlayerDied`, `KillFeed`,
  `BombStateUpdated`, `RoundStarted`, `RoundConcluded`, grenade events, and more.
- **Weapon data.** Every weapon carries its display name, fire mode and the `slotN` command that
  selects it, so your application needs no weapon table of its own.
- **JavaFX event viewer.** An included GUI to inspect events live.

## Requirements

- **Java 17** or newer
- **Maven 3.6.3+**, or the included `./mvnw` wrapper
- **Counter-Strike 2** (to actually emit game state)

## Project structure

This is a multi-module Maven project:

| Module    | Artifact         | Description                                                           |
| --------- | ---------------- | -------------------------------------------------------------------- |
| `lib`     | `cs2gsi`         | The core library that listens for and parses GSI events.             |
| `example` | `cs2gsi-example` | A runnable console program demonstrating common event subscriptions. |
| `viewer`  | `cs2gsi-viewer`  | A JavaFX UI for live-viewing selected GSI events.                    |

### Package overview (`lib`)

```
com.cs2gsi                 GameStateListener (entry point), GameState, CS2GSIFile
com.cs2gsi.events          Abstract event bases (CS2GameEvent, UpdateEvent, ...)
com.cs2gsi.events.player   Player state, weapons, kills, deaths, kill feed
com.cs2gsi.events.bomb     Bomb possession, planting, defusal, detonation
com.cs2gsi.events.grenade  Grenade lifecycle and trajectory
com.cs2gsi.events.map      Level, game mode, and match phase transitions
com.cs2gsi.events.round    Round lifecycle and phase countdowns
com.cs2gsi.events.team     Team scores, statistics, and round outcomes
com.cs2gsi.events.provider GSI provider and authentication updates
com.cs2gsi.nodes           Typed data model for the GSI JSON (Player, Round, ...)
```

## Building

Build all modules from the repository root:

```bash
mvn clean install
```

## Usage

### Via Maven Central (recommended)

The library is on Maven Central, so no extra repository is needed. Maven:

```xml
<dependency>
    <groupId>de.witzurke</groupId>
    <artifactId>cs2gsi</artifactId>
    <version>1.2.0</version>
</dependency>
```

Gradle:

```groovy
dependencies {
    implementation 'de.witzurke:cs2gsi:1.2.0'
}
```

The Java package is `com.cs2gsi`. The JAR declares the module name `com.cs2gsi` for the module path.

Coming from 1.1.0: `Weapon.slot` is now `Weapon.index`. Everything else in 1.2.0 is an addition, the
[changelog](CHANGELOG.md) has the list.

### Via JitPack

[JitPack](https://jitpack.io) builds the library from a Git tag. Add the JitPack
repository and the `cs2gsi` dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.bustolio.CS2-GSI</groupId>
    <artifactId>cs2gsi</artifactId>
    <version>1.2.0</version>
</dependency>
```

> Because this is a multi-module project, the JitPack group id is
> `com.github.bustolio.CS2-GSI` (repository) and the artifact id is `cs2gsi` (module).
> The version is the released Git tag.

For Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.bustolio.CS2-GSI:cs2gsi:1.2.0'
}
```

### Via a local build

`mvn clean install` puts the library into your local Maven repository under the same
`de.witzurke:cs2gsi` coordinates as on Maven Central.

### Listening for events

Start a listener and subscribe to events:

```java
import com.cs2gsi.GameStateListener;
import com.cs2gsi.events.player.PlayerGotKill;
import com.cs2gsi.events.round.RoundStarted;

public class Main {
    public static void main(String[] args) throws Exception {
        try (GameStateListener gsl = new GameStateListener(4000)) {
            // Write the gamestate_integration_*.cfg file CS2 needs.
            gsl.installGSIConfigFile("Example");

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
                System.out.println("Could not start. Is another program using port 4000?");
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

### Weapon data

The payload names a weapon `weapon_usp_silencer` and says nothing about how it fires.
`Weapon.info` looks the name up in the `WeaponInfo` enum:

```java
Weapon weapon = gsl.getCurrentGameState().player.getActiveWeapon();

System.out.println(weapon.info.displayName);   // "USP-S"
System.out.println(weapon.info.fireMode);      // SemiAutomatic
System.out.println("slot" + weapon.info.slot); // "slot2", the command that selects the pistol
```

- `fireMode` is `Automatic` if the weapon keeps firing on a held attack button, `SemiAutomatic` or
  `BoltAction` if it needs a click per shot, and `Revolver` for the R8, whose primary fire needs a
  held button. Grenades and the C4 have `Undefined`.
- `slot` is the group: 1 primary, 2 pistol, 3 knife and Zeus, 4 grenades, 5 C4. `directSlot` is
  the command for exactly that weapon where the game has one (6 HE, 7 flashbang, 8 smoke, 9 decoy,
  10 molotov and incendiary, 11 Zeus) and 0 otherwise. A player can leave the direct commands
  unbound, so fall back to `slot`.
- Every knife skin (`weapon_knife_karambit`, `weapon_bayonet`, ...) maps to `WeaponInfo.Knife`.
- A name the list does not know gives `WeaponInfo.Undefined` with an empty `displayName`, and so
  does the empty weapon that `getActiveWeapon()` returns when nothing is active. Nothing throws,
  so a weapon Valve adds later does not break your application. Fall back to `weapon.name` and
  `weapon.type` in that case.

`Weapon.index` is something else. It is the number from the `weapon_N` key, the position in the
payload.

### Local player or spectated player

The game fills `state.player` with whoever is on screen. While you spectate, that is another
player. The provider node always names the account that runs the game, and `isLocalPlayer()`
compares the two:

```java
gsl.onNewGameState(state -> {
    if (state.isLocalPlayer()) {
        // state.player is you
    }
});
```

The method returns `true` only if both Steam IDs are present and equal. With a missing ID it
returns `false`, because nothing shows that the player node is the local one. The generated
configuration file enables both nodes.

### Is the game still sending?

`getCurrentGameState()` keeps returning the last state when the game stops sending, for example
after a game update broke the integration or the cfg file was deleted. `getLastGameStateTime()`
tells the two apart:

```java
boolean gameIsSending = gsl.getLastGameStateTime()
        .map(time -> Duration.between(time, Instant.now()).getSeconds() < 25)
        .orElse(false);
```

The generated configuration file sets a heartbeat of 10 seconds, so the game sends at least that
often while it runs. A heartbeat that repeats the previous state raises no event but still
counts here. For tests, `new GameStateListener(port, clock)` takes a `java.time.InstantSource`
that supplies the time.

### Running the example

```bash
mvn -pl example -am clean package
java -jar example/target/cs2gsi-example-1.2.0.jar
```

> `start()` returns `false` when it cannot bind the address. The usual cause is another program
> on the same port. The listener binds to the loopback address and needs no administrator rights.

### Running the JavaFX viewer

Install the modules first, then invoke the `javafx:run` goal on the viewer module alone
(plugin goals, unlike lifecycle phases, cannot be combined with `-am`):

```bash
mvn clean install
mvn -pl viewer javafx:run
```

## Configuring Counter-Strike 2

`installGSIConfigFile(...)` writes a `gamestate_integration_*.cfg` file into the game's
`csgo/cfg` directory, pointing CS2 at your listener (e.g. `http://127.0.0.1:4000/`). The port
passed to `GameStateListener` must match the one in the file.

The method returns a `GSIConfigResult` with the file path and a status of `CREATED`, `UPDATED`,
`UNCHANGED` or `FAILED`. It skips the write when the content already matches. CS2 reads the file
only at startup, so a running game needs a restart after `CREATED` or `UPDATED`. On `FAILED`,
`cause()` holds the exception, for example when CS2 is not installed or the folder is read-only.

The older `generateGSIConfigFile(...)` does the same and returns only a boolean. It is deprecated.

### Authentication token

Without a token, every program on the machine can post game states to the listener. Set one
before you install the configuration file:

```java
gsl.setAuthToken("a-long-random-string");
gsl.installGSIConfigFile("Example");
```

The file then carries an `auth` block, the game sends the token with every update, and the
listener answers 401 to updates without it.

## Threading and errors

All handlers run on one daemon thread named `CS2GSI-GameStateListener`, in the order the game
sent its updates. A handler that blocks delays every later update, and the game drops a request
after the `timeout` in the configuration file, which is 5 seconds in the generated one. Hand slow
work to another thread. Switch to the UI thread yourself before you touch JavaFX or Swing, for
example with `Platform.runLater`. A daemon thread does not keep the JVM alive, which is why the
example above waits on `System.in`.

A handler that throws does not stop the other handlers. The exception goes to the thread's
uncaught exception handler, which prints it to `System.err` unless you installed your own with
`Thread.setDefaultUncaughtExceptionHandler`.

The listener accepts POST requests of up to 4 MiB and refuses requests that carry an `Origin`
header, so a web page open in a browser on the same machine cannot feed it made-up game states.

## License

MIT, see [LICENSE](LICENSE).
