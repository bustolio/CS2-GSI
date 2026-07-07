package com.cs2gsi.events.bomb;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Bomb update.
 */
public class BombUpdated extends UpdateEvent<Bomb> {
    public BombUpdated(Bomb newValue, Bomb previousValue) {
        super(newValue, previousValue);
    }
}
