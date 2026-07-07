package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's flash amount update.
 */
public class PlayerFlashAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerFlashAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
