package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's MVPs statistic update.
 */
public class PlayerMVPsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerMVPsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
