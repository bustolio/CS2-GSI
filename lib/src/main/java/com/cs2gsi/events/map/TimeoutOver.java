package com.cs2gsi.events.map;

import com.cs2gsi.events.CS2GameEvent;

import com.cs2gsi.nodes.PlayerTeam;

/**
 * Event for game Timeout ending.
 */
public class TimeoutOver extends CS2GameEvent {
    /**
     * The team the timeout is started by.
     */
    public final PlayerTeam team;

    public TimeoutOver(PlayerTeam team) {
        this.team = team;
    }
}
