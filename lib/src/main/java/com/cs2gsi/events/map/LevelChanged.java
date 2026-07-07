package com.cs2gsi.events;

/**
 * Event for level change.
 */
public class LevelChanged extends UpdateEvent<String> {
    public LevelChanged(String newValue, String previousValue) {
        super(newValue, previousValue);
    }
}
