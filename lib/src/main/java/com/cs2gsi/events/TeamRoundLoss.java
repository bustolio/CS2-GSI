package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's Round loss.
 */
public class TeamRoundLoss extends TeamValueEvent<Integer> {
    public TeamRoundLoss(Integer value, PlayerTeam team) {
        super(value, team);
    }
}
