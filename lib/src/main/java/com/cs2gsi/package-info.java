/**
 * Core entry points for Counter-Strike 2 Game State Integration.
 * <p>
 * {@link com.cs2gsi.GameStateListener} is the main entry point: it receives the
 * HTTP POST requests Counter-Strike 2 sends, parses them into
 * {@link com.cs2gsi.GameState} objects, and dispatches granular game events.
 * Subscriptions are managed through the {@link com.cs2gsi.CS2EventsInterface}
 * contract it extends, and {@link com.cs2gsi.CS2GSIFile} generates the
 * {@code gamestate_integration_*.cfg} file the game needs.
 */
package com.cs2gsi;
