package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's smoked amount update.
 */
public class PlayerSmokedAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerSmokedAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
