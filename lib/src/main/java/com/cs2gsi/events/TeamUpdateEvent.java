package com.cs2gsi.events;

import com.cs2gsi.nodes.PlayerTeam;

/**
 * Event for specific team's value change.
 *
 * @param <T> The value type.
 */
public class TeamUpdateEvent<T> extends UpdateEvent<T> {
    /**
     * The associated team.
     */
    public final PlayerTeam team;

    public TeamUpdateEvent(T newValue, T previousValue, PlayerTeam team) {
        super(newValue, previousValue);
        this.team = team;
    }
}
