package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.helpers.Vector3D;

/**
 * Event for specific player's forward direction change.
 */
public class PlayerForwardDirectionChanged extends PlayerUpdateEvent<Vector3D> {
    public PlayerForwardDirectionChanged(Vector3D newValue, Vector3D previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
