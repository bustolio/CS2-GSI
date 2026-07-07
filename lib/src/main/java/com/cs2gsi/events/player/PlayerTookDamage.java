package com.cs2gsi.events.player;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's taking damage.
 */
public class PlayerTookDamage extends PlayerHealthChanged {
    public PlayerTookDamage(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
