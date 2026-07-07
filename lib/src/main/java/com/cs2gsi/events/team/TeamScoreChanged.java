package com.cs2gsi.events.team;

import com.cs2gsi.events.TeamUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's score change.
 */
public class TeamScoreChanged extends TeamUpdateEvent<Integer> {
    public TeamScoreChanged(Integer newValue, Integer previousValue, PlayerTeam team) {
        super(newValue, previousValue, team);
    }
}
