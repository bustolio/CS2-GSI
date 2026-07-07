package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityUpdateEvent;

import com.cs2gsi.nodes.helpers.Vector3D;

/**
 * Event for a specific grenade's velocity change.
 */
public class GrenadeVelocityChanged extends EntityUpdateEvent<Vector3D> {
    public GrenadeVelocityChanged(Vector3D newValue, Vector3D previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
