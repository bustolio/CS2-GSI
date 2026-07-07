/**
 * The subscribable game event hierarchy, rooted at
 * {@link com.cs2gsi.events.CS2GameEvent}.
 * <p>
 * This package holds the abstract bases — {@link com.cs2gsi.events.ValueEvent},
 * {@link com.cs2gsi.events.UpdateEvent}, and their player/team/entity
 * specializations — which carry the new and previous values of a change.
 * The concrete events raised by diffing consecutive game states live in
 * thematic subpackages:
 * <ul>
 *   <li>{@link com.cs2gsi.events.player} – player state, weapons, kills, connections</li>
 *   <li>{@link com.cs2gsi.events.bomb} – bomb possession, planting, defusal, detonation</li>
 *   <li>{@link com.cs2gsi.events.grenade} – grenade lifecycle and trajectory</li>
 *   <li>{@link com.cs2gsi.events.map} – level, game mode, and match phase transitions</li>
 *   <li>{@link com.cs2gsi.events.round} – round lifecycle and phase countdowns</li>
 *   <li>{@link com.cs2gsi.events.team} – team scores, statistics, and round outcomes</li>
 *   <li>{@link com.cs2gsi.events.provider} – GSI provider and authentication data</li>
 * </ul>
 * Subscribing to a base event type also receives its subtypes.
 */
package com.cs2gsi.events;
