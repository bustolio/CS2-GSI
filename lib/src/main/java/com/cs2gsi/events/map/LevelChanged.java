package com.cs2gsi.events.map;

import com.cs2gsi.events.UpdateEvent;

/**
 * Event for level change.
 */
public class LevelChanged extends UpdateEvent<String> {
    public LevelChanged(String newValue, String previousValue) {
        super(newValue, previousValue);
    }
}
