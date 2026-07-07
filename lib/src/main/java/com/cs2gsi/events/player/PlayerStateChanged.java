package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's state update.
 */
public class PlayerStateChanged extends PlayerUpdateEvent<PlayerState> {
    public PlayerStateChanged(PlayerState newValue, PlayerState previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
