package com.cs2gsi.events.round;

import com.cs2gsi.events.UpdateEvent;

/**
 * Event for round change.
 */
public class RoundChanged extends UpdateEvent<Integer> {
    public RoundChanged(Integer newValue, Integer previousValue) {
        super(newValue, previousValue);
    }
}
