package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's round total damage update.
 */
public class PlayerRoundTotalDamageChanged extends PlayerUpdateEvent<Integer> {
    public PlayerRoundTotalDamageChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
