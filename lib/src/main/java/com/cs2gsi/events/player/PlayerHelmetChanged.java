package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's helmet update.
 */
public class PlayerHelmetChanged extends PlayerUpdateEvent<Boolean> {
    public PlayerHelmetChanged(Boolean newValue, Boolean previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
