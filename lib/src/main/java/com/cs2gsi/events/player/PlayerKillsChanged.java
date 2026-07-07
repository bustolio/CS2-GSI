package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's Kills statistic update.
 */
public class PlayerKillsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerKillsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
