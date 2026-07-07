package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's spectation target change.
 */
public class PlayerSpectationTargetChanged extends PlayerUpdateEvent<String> {
    public PlayerSpectationTargetChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
