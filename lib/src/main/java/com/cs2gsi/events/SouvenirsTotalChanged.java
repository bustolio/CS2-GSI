package com.cs2gsi.events;

/**
 * Event for the total amount of souvenirs change.
 */
public class SouvenirsTotalChanged extends UpdateEvent<Integer> {
    public SouvenirsTotalChanged(Integer newValue, Integer previousValue) {
        super(newValue, previousValue);
    }
}
