package com.cs2gsi.events.player;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's death.
 */
public class PlayerDied extends PlayerHealthChanged {
    public PlayerDied(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
