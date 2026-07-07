package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for the observed player's Steam ID change (spectator target switch).
 */
public class PlayerSteamIDChanged extends PlayerUpdateEvent<String> {
    public PlayerSteamIDChanged(String newValue, String previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
