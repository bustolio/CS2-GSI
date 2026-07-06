package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;

/**
 * Event for the bomb carrier change.
 */
public class BombPlayerChanged extends UpdateEvent<Player> {
    public BombPlayerChanged(Player newValue, Player previousValue) {
        super(newValue, previousValue);
    }
}
