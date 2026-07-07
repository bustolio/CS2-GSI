package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for the observed player's Steam ID change (spectator target switch).
 */
public class PlayerSteamIDChanged extends PlayerUpdateEvent<String> {
    public PlayerSteamIDChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
