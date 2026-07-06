package com.cs2gsi.events;

import com.cs2gsi.nodes.RoundConclusion;

import java.util.Map;

/**
 * Event for the round conclusions history change.
 */
public class RoundWinsChanged extends UpdateEvent<Map<Integer, RoundConclusion>> {
    public RoundWinsChanged(Map<Integer, RoundConclusion> newValue, Map<Integer, RoundConclusion> previousValue) {
        super(newValue, previousValue);
    }
}
