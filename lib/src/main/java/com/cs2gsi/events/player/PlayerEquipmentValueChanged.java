package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's equipment value update.
 */
public class PlayerEquipmentValueChanged extends PlayerUpdateEvent<Integer> {
    public PlayerEquipmentValueChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
