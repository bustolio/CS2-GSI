package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's armor update.
 */
public class PlayerArmorChanged extends PlayerUpdateEvent<Integer> {
    public PlayerArmorChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
