package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's flash amount update.
 */
public class PlayerFlashAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerFlashAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
