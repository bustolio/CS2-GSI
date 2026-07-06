package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's MVPs statistic update.
 */
public class PlayerMVPsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerMVPsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
