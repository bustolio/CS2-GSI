package com.cs2gsi.events.bomb;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.Player;

/**
 * Event for the bomb carrier change.
 */
public class BombPlayerChanged extends UpdateEvent<Player> {
    public BombPlayerChanged(Player newValue, Player previousValue) {
        super(newValue, previousValue);
    }
}
