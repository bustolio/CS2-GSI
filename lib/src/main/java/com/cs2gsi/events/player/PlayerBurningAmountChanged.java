package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's burning amount update.
 */
public class PlayerBurningAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerBurningAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
