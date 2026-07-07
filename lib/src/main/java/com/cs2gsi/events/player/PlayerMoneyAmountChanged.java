package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's money amount update.
 */
public class PlayerMoneyAmountChanged extends PlayerUpdateEvent<Integer> {
    public PlayerMoneyAmountChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
