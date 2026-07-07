package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's activity state update.
 */
public class PlayerActivityChanged extends PlayerUpdateEvent<PlayerActivity> {
    public PlayerActivityChanged(PlayerActivity newValue, PlayerActivity previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
