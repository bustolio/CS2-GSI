package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's Assists statistic update.
 */
public class PlayerAssistsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerAssistsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
