# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
- Reduced the public API surface of `lib`: internal event-dispatch and diff-handler
  classes (`EventDispatcher`, `EventHandler`, `GameStateHandler`, and the per-section
  `*Handler` classes) are now package-private.
- Relocated `ACF` and `SteamUtils` from `com.cs2gsi.utils` into `com.cs2gsi` as
  package-private implementation details; the `com.cs2gsi.utils` package is removed.
- Added `url`/`scm`/`developers` metadata to the Maven POMs.

### Fixed

- Removed `example/dependency-reduced-pom.xml`, a `maven-shade-plugin` build artifact
  that was accidentally tracked in git; it is now ignored.
