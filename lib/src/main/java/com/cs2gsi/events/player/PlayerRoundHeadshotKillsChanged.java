package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's round headshot kills update.
 */
public class PlayerRoundHeadshotKillsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerRoundHeadshotKillsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
