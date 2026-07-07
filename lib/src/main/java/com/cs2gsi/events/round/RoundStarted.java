package com.cs2gsi.events.round;

import com.cs2gsi.events.CS2GameEvent;

/**
 * Event for round starting.
 */
public class RoundStarted extends CS2GameEvent {
    /**
     * The round.
     */
    public final int round;

    /**
     * Is this the first round?
     */
    public final boolean isFirstRound;

    /**
     * Is this the last round (or last round of half)?
     */
    public final boolean isLastRound;

    public RoundStarted(int round, boolean isFirstRound, boolean isLastRound) {
        this.round = round;
        this.isFirstRound = isFirstRound;
        this.isLastRound = isLastRound;
    }
}
