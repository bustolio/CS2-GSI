package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's Deaths statistic update.
 */
public class PlayerDeathsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerDeathsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
