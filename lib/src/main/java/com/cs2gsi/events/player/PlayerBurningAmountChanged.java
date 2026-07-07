package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's burning amount update.
 */
public class PlayerBurningAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerBurningAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
