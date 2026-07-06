package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for Bomb having been picked up.
 */
public class BombPickedup extends CS2GameEvent {
    /**
     * The associated player.
     */
    public final Player player;

    public BombPickedup(Player player) {
        this.player = player;
    }
}
