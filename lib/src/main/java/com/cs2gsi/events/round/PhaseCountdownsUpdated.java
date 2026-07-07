package com.cs2gsi.events.round;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Phase Countdowns update.
 */
public class PhaseCountdownsUpdated extends UpdateEvent<PhaseCountdowns> {
    public PhaseCountdownsUpdated(PhaseCountdowns newValue, PhaseCountdowns previousValue) {
        super(newValue, previousValue);
    }
}
