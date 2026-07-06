package com.cs2gsi.events;

import com.cs2gsi.nodes.PlayerTeam;

/**
 * Event for specific team's single value update.
 *
 * @param <T> The value type.
 */
public class TeamValueEvent<T> extends ValueEvent<T> {
    /**
     * The associated team.
     */
    public final PlayerTeam team;

    public TeamValueEvent(T value, PlayerTeam team) {
        super(value);
        this.team = team;
    }
}
