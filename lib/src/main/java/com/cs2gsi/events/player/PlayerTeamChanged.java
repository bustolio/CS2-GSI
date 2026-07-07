package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's team change.
 */
public class PlayerTeamChanged extends PlayerUpdateEvent<PlayerTeam> {
    public PlayerTeamChanged(PlayerTeam newValue, PlayerTeam previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
