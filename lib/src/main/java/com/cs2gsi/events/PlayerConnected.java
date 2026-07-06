package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for Player connecting to the game.
 */
public class PlayerConnected extends ValueEvent<Player> {
    public PlayerConnected(Player value) {
        super(value);
    }
}
