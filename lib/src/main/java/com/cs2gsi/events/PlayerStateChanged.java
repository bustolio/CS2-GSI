package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's state update.
 */
public class PlayerStateChanged extends PlayerUpdateEvent<PlayerState> {
    public PlayerStateChanged(PlayerState newValue, PlayerState previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
