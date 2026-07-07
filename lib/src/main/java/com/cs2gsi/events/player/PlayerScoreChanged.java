package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's Score statistic update.
 */
public class PlayerScoreChanged extends PlayerUpdateEvent<Integer> {
    public PlayerScoreChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
