package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's spectation target change.
 */
public class PlayerSpectationTargetChanged extends PlayerUpdateEvent<String> {
    public PlayerSpectationTargetChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
