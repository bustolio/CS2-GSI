package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's XP Overload level change.
 */
public class PlayerXPOverloadLevelChanged extends PlayerUpdateEvent<Integer> {
    public PlayerXPOverloadLevelChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
