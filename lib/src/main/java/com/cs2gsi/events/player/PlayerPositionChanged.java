package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.helpers.Vector3D;

/**
 * Event for specific player's position change.
 */
public class PlayerPositionChanged extends PlayerUpdateEvent<Vector3D> {
    public PlayerPositionChanged(Vector3D newValue, Vector3D previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
