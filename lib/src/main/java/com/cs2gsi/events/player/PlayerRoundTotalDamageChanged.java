package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's round total damage update.
 */
public class PlayerRoundTotalDamageChanged extends PlayerUpdateEvent<Integer> {
    public PlayerRoundTotalDamageChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
