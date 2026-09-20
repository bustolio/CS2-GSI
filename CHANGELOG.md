# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.0] - 2026-09-21

### Added

- `Weapon.info`, a `WeaponInfo` enum entry found by the payload name. It carries the payload name,
  a display name (`AK-47`, `USP-S`) and a `FireMode` that says whether the primary fire keeps
  going on a held attack button (`Automatic`), needs a click per shot (`SemiAutomatic`,
  `BoltAction`) or is the R8 special case (`Revolver`). Every knife skin maps to
  `WeaponInfo.Knife`. Unknown names and the empty weapon give `WeaponInfo.Undefined`, nothing
  throws.
- `WeaponInfo.slot` and `WeaponInfo.directSlot`, the numbers of the game's `slotN` commands.
  `slot` selects the group (1 primary, 2 pistol, 3 knife and Zeus, 4 grenades, 5 C4),
  `directSlot` selects exactly that weapon where the game has such a command (6 to 10 for the
  grenades, 11 for the Zeus) and is 0 otherwise.
- `GameState.isLocalPlayer()`. True only if the provider node and the player node carry the same
  Steam ID, so it is false while spectating and false if either ID is missing.
- `GameStateListener.getLastGameStateTime()`, the time of the last accepted game state, empty
  before the first one. A heartbeat that repeats the previous state counts, so an application can
  tell a quiet freeze time from a game that stopped sending. Two new constructors take a
  `java.time.InstantSource`, so a test can set the time.

### Changed

- **Breaking:** `Weapon.slot` is now `Weapon.index`. The field holds the number from the
  `weapon_N` key, which is the position in the payload and not the slot a `slotN` command selects.
  Code that reads `weapon.slot` has to read `weapon.index`, or `weapon.info.slot` if it wanted the
  real slot. `Weapon.toString()` prints `Index:` where it printed `Slot:`.

### Fixed

- The precision settings go into an `output` block of the configuration file. The game ignores
  the flat `output/precision_time`, `output/precision_position` and `output/precision_vector` keys
  that 1.1.0 and earlier wrote. Measured with CS2 build 25218825: a flat key asking for 4 decimals
  left positions at 2, the block delivered 4. The values stay at the game's defaults, so game
  states look the same, but `installFile` reports `UPDATED` once for every existing file.
- `Weapon.type` is `SubmachineGun` and `MachineGun` for SMGs and machine guns. The game writes
  these two types as `"Submachine Gun"` and `"Machine Gun"`, and `Node.toEnum` compared them
  against the constant names with the space still in, so both came out as `Undefined`.

## [1.1.0] - 2026-09-20

### Added

- `GameStateListener.installGSIConfigFile(String)` and `CS2GSIFile.installFile(...)`. Both return
  a `GSIConfigResult` with the file path, a status of `CREATED`, `UPDATED`, `UNCHANGED` or
  `FAILED`, and the cause of a failure, so an application knows whether a running game needs a
  restart and why an installation did not work.
- `GameStateListener.setAuthToken(String)`. The token goes into the `auth` block of the
  configuration file, and the listener answers 401 to game states that do not carry it.
- `Automatic-Module-Name: com.cs2gsi` in the JAR manifest.
- MIT license (`LICENSE`, `<licenses>` in the POM).
- `release` Maven profile that signs the artifacts and uploads them to Maven Central.
- Tests for `MapHandler`, `BombHandler`, `AllPlayersHandler`, `KillfeedHandler`, the
  `previously` block, and listener restart, request filtering and handler failures.

### Changed

- The Maven groupId is now `de.witzurke` (was `com.cs2gsi`). Builds that depend on a locally
  installed `com.cs2gsi:cs2gsi` need the new coordinates `de.witzurke:cs2gsi`. The JitPack
  coordinates and the Java package `com.cs2gsi` stay the same.
- `generateGSIConfigFile` and `CS2GSIFile.createFile` no longer rewrite the config file when its
  content already matches. They still return `true` in that case.
- The listener accepts only POST requests of up to 4 MiB and refuses requests with an `Origin`
  header, so a web page in a local browser cannot post made-up game states.
- An exception thrown by a handler goes to the thread's uncaught exception handler. It used to
  vanish, and it kept the remaining handlers from seeing that game state.
- Handlers run outside the listener's internal lock. A handler may now wait for another thread
  that calls `getCurrentGameState()`.
- `getPort()` returns the bound port while the listener runs, which matters for port 0.
- `Player.weapons` is an unmodifiable list.
- Gson 2.13.2 (was 2.11.0), `maven-compiler-plugin` 3.14.1 (was 3.8.1), reproducible JARs,
  compiler lint warnings on, Checkstyle violations fail the build, CI on Linux and Windows
  with JDK 17, 21 and 25 through the Maven wrapper.

### Deprecated

- `GameStateListener.generateGSIConfigFile` and both `CS2GSIFile.createFile` overloads, in
  favour of `installGSIConfigFile` and `installFile`.

### Removed

- `example/dependency-reduced-pom.xml` from git, and the internal `AuthHandler`, which did nothing.

### Fixed

- Integration names containing `/`, `\` or `"` are rejected. A separator let the file leave the
  game's `cfg` folder.
- Quotes and backslashes are escaped when the configuration file is written, and the parser
  keeps a backslash that does not start an escape sequence. A library path written as
  `D:\SteamLibrary` used to be read as `D:teamLibrary`.
- `BombPlanted`, `BombDefused` and `BombExploded` fired twice when a payload carried both the
  `round` and the `bomb` block. An aborted defuse no longer counts as a second plant.
- Observers got every event of the spectated player twice, once from the `player` block and
  once from `allplayers`.
- `RoundConcluded.isFirstRound` and `isLastRound` described the round that started instead of
  the one that ended.
- `Node.equals` returned `false` for an empty node compared with itself.
- `getPreviousGameState()` and `GameState.getPreviously()` are safe to call from other threads.
- The Steam lookup starts `reg.exe` by its full path with a 5 second limit, and falls back to
  the default Steam folders when the registry value is unusable.
- The README told Windows users to run as Administrator when `start()` fails. The listener
  binds a loopback port and needs no elevation. The usual cause is a port already in use.

## [1.0.0] - 2026-07-07

### Added

- JUnit 5 test suite for the `lib` module covering event dispatch, GSI JSON parsing,
  round/player diff handlers, ACF serialization, config-file generation, and an
  HTTP end-to-end test of `GameStateListener`.
- GitHub Actions CI workflow building and testing all modules on push and pull request.
- `maven-source-plugin` / `maven-javadoc-plugin` bindings so the library publishes
  `-sources.jar` and `-javadoc.jar` alongside the main artifact.
- Report-only Checkstyle configuration (`lib/checkstyle.xml`) run during `mvn verify`.
- `package-info.java` documentation for `com.cs2gsi`, `com.cs2gsi.events`,
  `com.cs2gsi.nodes`, and `com.cs2gsi.nodes.helpers`.
- `.editorconfig` and this changelog.

### Changed

- Adopted semantic versioning (`1.0` → `1.0.0`).
- Grouped the concrete event classes into thematic subpackages
  (`events.player`, `events.bomb`, `events.grenade`, `events.map`,
  `events.round`, `events.team`, `events.provider`); the abstract event bases
  remain in `com.cs2gsi.events`. Import paths of concrete events change
  accordingly.
- Reduced the public API surface of `lib`: internal event-dispatch and diff-handler
  classes (`EventDispatcher`, `EventHandler`, `GameStateHandler`, and the per-section
  `*Handler` classes) are now package-private.
- Relocated `ACF` and `SteamUtils` from `com.cs2gsi.utils` into `com.cs2gsi` as
  package-private implementation details; the `com.cs2gsi.utils` package is removed.
- Added `url`/`scm`/`developers` metadata to the Maven POMs.

### Fixed

- Added `example/dependency-reduced-pom.xml`, a `maven-shade-plugin` build artifact, to
  `.gitignore`. The file stayed tracked until 1.1.0.
