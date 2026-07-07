package com.cs2gsi.events.team;

import com.cs2gsi.events.TeamValueEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's Round loss.
 */
public class TeamRoundLoss extends TeamValueEvent<Integer> {
    public TeamRoundLoss(Integer value, PlayerTeam team) {
        super(value, team);
    }
}
