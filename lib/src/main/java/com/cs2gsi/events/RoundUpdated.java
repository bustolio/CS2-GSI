package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Round update.
 */
public class RoundUpdated extends UpdateEvent<Round> {
    public RoundUpdated(Round newValue, Round previousValue) {
        super(newValue, previousValue);
    }
}
