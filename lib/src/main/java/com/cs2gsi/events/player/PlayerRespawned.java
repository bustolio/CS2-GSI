package com.cs2gsi.events.player;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's respawn.
 */
public class PlayerRespawned extends PlayerHealthChanged {
    public PlayerRespawned(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
