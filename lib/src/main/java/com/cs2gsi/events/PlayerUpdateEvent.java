package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's value change.
 *
 * @param <T> The value type.
 */
public class PlayerUpdateEvent<T> extends UpdateEvent<T> {
    /**
     * The associated player.
     */
    public final Player player;

    public PlayerUpdateEvent(T newValue, T previousValue, Player player) {
        super(newValue, previousValue);
        this.player = player;
    }
}
