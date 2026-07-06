package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player update.
 */
public class PlayerEvent extends CS2GameEvent {
    /**
     * The associated player.
     */
    public final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }
}
