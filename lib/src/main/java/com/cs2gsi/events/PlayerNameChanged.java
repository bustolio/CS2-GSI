package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for specific player's name change.
 */
public class PlayerNameChanged extends PlayerUpdateEvent<String> {
    public PlayerNameChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
