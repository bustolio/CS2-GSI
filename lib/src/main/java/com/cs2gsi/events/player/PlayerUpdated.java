package com.cs2gsi.events.player;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for overall Player update.
 */
public class PlayerUpdated extends UpdateEvent<Player> {
    /**
     * The player ID of the updated player.
     */
    public final String playerId;

    public PlayerUpdated(Player newValue, Player previousValue, String playerId) {
        super(newValue, previousValue);
        this.playerId = playerId;
    }
}
