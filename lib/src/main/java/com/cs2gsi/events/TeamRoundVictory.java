package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's Round victory.
 */
public class TeamRoundVictory extends TeamValueEvent<Integer> {
    public TeamRoundVictory(Integer value, PlayerTeam team) {
        super(value, team);
    }
}
