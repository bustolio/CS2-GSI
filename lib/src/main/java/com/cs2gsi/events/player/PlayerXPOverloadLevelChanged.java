package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's XP Overload level change.
 */
public class PlayerXPOverloadLevelChanged extends PlayerUpdateEvent<Integer> {
    public PlayerXPOverloadLevelChanged(Integer newValue, Integer previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
