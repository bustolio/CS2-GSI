package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityUpdateEvent;

import com.cs2gsi.nodes.helpers.Vector3D;

/**
 * Event for a specific grenade's position change.
 */
public class GrenadePositionChanged extends EntityUpdateEvent<Vector3D> {
    public GrenadePositionChanged(Vector3D newValue, Vector3D previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
