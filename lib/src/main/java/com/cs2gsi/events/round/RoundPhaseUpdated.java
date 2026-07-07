package com.cs2gsi.events.round;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for Round Phase update.
 */
public class RoundPhaseUpdated extends UpdateEvent<Phase> {
    public RoundPhaseUpdated(Phase newValue, Phase previousValue) {
        super(newValue, previousValue);
    }
}
