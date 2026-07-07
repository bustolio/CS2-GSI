package com.cs2gsi.events.team;

import com.cs2gsi.events.TeamUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's remaining timeouts change.
 */
public class TeamRemainingTimeoutsChanged extends TeamUpdateEvent<Integer> {
    public TeamRemainingTimeoutsChanged(Integer newValue, Integer previousValue, PlayerTeam team) {
        super(newValue, previousValue, team);
    }
}
