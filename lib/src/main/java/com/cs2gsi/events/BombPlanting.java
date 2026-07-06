package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for Bomb being planted.
 */
public class BombPlanting extends CS2GameEvent {
    /**
     * The associated player.
     */
    public final Player player;

    public BombPlanting(Player player) {
        this.player = player;
    }
}
