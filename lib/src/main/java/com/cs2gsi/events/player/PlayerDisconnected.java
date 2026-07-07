package com.cs2gsi.events.player;

import com.cs2gsi.events.ValueEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for Player disconnecting from the game.
 */
public class PlayerDisconnected extends ValueEvent<Player> {
    public PlayerDisconnected(Player value) {
        super(value);
    }
}
