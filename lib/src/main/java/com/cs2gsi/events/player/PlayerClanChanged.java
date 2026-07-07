package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's clan change.
 */
public class PlayerClanChanged extends PlayerUpdateEvent<String> {
    public PlayerClanChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
