package com.cs2gsi.events;

/**
 * Event for the phase end time countdown change.
 */
public class PhaseEndTimeChanged extends UpdateEvent<Float> {
    public PhaseEndTimeChanged(Float newValue, Float previousValue) {
        super(newValue, previousValue);
    }
}
