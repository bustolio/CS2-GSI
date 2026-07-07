package com.cs2gsi.events.bomb;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for Bomb State update.
 */
public class BombStateUpdated extends UpdateEvent<BombState> {
    public BombStateUpdated(BombState newValue, BombState previousValue) {
        super(newValue, previousValue);
    }
}
