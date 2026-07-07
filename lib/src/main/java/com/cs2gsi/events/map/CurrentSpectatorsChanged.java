package com.cs2gsi.events;

/**
 * Event for the amount of current spectators change.
 */
public class CurrentSpectatorsChanged extends UpdateEvent<Integer> {
    public CurrentSpectatorsChanged(Integer newValue, Integer previousValue) {
        super(newValue, previousValue);
    }
}
