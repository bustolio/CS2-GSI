package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's observer slot change.
 */
public class PlayerObserverSlotChanged extends PlayerUpdateEvent<Integer> {
    public PlayerObserverSlotChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
