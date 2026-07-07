package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's equipment value update.
 */
public class PlayerEquipmentValueChanged extends PlayerUpdateEvent<Integer> {
    public PlayerEquipmentValueChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
