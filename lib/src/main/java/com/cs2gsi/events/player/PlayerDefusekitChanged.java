package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's defuse kit update.
 */
public class PlayerDefusekitChanged extends PlayerUpdateEvent<Boolean> {
    public PlayerDefusekitChanged(Boolean newValue, Boolean previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
