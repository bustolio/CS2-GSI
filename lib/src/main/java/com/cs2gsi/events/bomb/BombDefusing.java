package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for Bomb being defused.
 */
public class BombDefusing extends CS2GameEvent {
    /**
     * The associated player.
     */
    public final Player player;

    public BombDefusing(Player player) {
        this.player = player;
    }
}
