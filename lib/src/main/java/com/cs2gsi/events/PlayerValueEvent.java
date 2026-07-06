package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's single value update.
 *
 * @param <T> The value type.
 */
public class PlayerValueEvent<T> extends ValueEvent<T> {
    /**
     * The associated player.
     */
    public final Player player;

    public PlayerValueEvent(T value, Player player) {
        super(value);
        this.player = player;
    }
}
