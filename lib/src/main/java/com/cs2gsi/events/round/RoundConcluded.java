package com.cs2gsi.events;

import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.RoundConclusion;

/**
 * Event for round conclusion.
 */
public class RoundConcluded extends CS2GameEvent {
    /**
     * The round.
     */
    public final int round;

    /**
     * The reason for round conclusion.
     */
    public final RoundConclusion roundConclusionReason;

    /**
     * The winning team of the round.
     */
    public final PlayerTeam winningTeam;

    /**
     * Is this the first round?
     */
    public final boolean isFirstRound;

    /**
     * Is this the last round (or last round of half)?
     */
    public final boolean isLastRound;

    public RoundConcluded(int round, RoundConclusion conclusion, PlayerTeam winningTeam,
                          boolean isFirstRound, boolean isLastRound) {
        this.round = round;
        this.roundConclusionReason = conclusion;
        this.winningTeam = winningTeam;
        this.isFirstRound = isFirstRound;
        this.isLastRound = isLastRound;
    }
}
