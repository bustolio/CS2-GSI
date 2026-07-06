package com.cs2gsi.events;

/**
 * Event for the provider timestamp change.
 */
public class ProviderTimestampChanged extends UpdateEvent<Integer> {
    public ProviderTimestampChanged(Integer newValue, Integer previousValue) {
        super(newValue, previousValue);
    }
}
