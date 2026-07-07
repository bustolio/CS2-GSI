/**
 * The subscribable game event hierarchy, rooted at
 * {@link com.cs2gsi.events.CS2GameEvent}.
 * <p>
 * Concrete leaf events (for example {@link com.cs2gsi.events.PlayerGotKill} or
 * {@link com.cs2gsi.events.RoundStarted}) are raised by diffing consecutive game
 * states. Generic intermediate bases such as {@link com.cs2gsi.events.UpdateEvent}
 * and {@link com.cs2gsi.events.ValueEvent} carry the new and previous values.
 * Subscribing to a base event type also receives its subtypes.
 */
package com.cs2gsi.events;
