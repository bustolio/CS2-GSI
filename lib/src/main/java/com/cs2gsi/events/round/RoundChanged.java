package com.cs2gsi.events;

/**
 * Event for round change.
 */
public class RoundChanged extends UpdateEvent<Integer> {
    public RoundChanged(Integer newValue, Integer previousValue) {
        super(newValue, previousValue);
    }
}
