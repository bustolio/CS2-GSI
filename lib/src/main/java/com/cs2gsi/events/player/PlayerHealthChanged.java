package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's health update.
 */
public class PlayerHealthChanged extends PlayerUpdateEvent<Integer> {
    public PlayerHealthChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
