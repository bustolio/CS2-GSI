package com.cs2gsi.events.map;

import com.cs2gsi.events.CS2GameEvent;

import com.cs2gsi.nodes.PlayerTeam;

/**
 * Event for game Timeout starting.
 */
public class TimeoutStarted extends CS2GameEvent {
    /**
     * The team the timeout is started by.
     */
    public final PlayerTeam team;

    public TimeoutStarted(PlayerTeam team) {
        this.team = team;
    }
}
