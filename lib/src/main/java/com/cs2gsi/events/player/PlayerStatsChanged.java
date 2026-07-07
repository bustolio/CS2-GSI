package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's statistics update.
 */
public class PlayerStatsChanged extends PlayerUpdateEvent<MatchStats> {
    public PlayerStatsChanged(MatchStats newValue, MatchStats previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
