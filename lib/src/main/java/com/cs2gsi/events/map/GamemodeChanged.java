package com.cs2gsi.events.map;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for gamemode changing.
 */
public class GamemodeChanged extends UpdateEvent<GameMode> {
    public GamemodeChanged(GameMode newValue, GameMode previousValue) {
        super(newValue, previousValue);
    }
}
