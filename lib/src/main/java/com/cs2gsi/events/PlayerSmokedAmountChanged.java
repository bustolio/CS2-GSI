package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's smoked amount update.
 */
public class PlayerSmokedAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerSmokedAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
