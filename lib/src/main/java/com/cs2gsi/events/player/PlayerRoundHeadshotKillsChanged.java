package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's round headshot kills update.
 */
public class PlayerRoundHeadshotKillsChanged extends PlayerUpdateEvent<Integer> {
    public PlayerRoundHeadshotKillsChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
