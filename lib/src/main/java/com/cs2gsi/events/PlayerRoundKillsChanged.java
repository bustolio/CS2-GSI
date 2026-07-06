package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's round kills update.
 */
public class PlayerRoundKillsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerRoundKillsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
