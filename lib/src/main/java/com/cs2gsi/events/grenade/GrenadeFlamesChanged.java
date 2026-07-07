package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityUpdateEvent;

import com.cs2gsi.nodes.helpers.Vector3D;

import java.util.Map;

/**
 * Event for a specific grenade's flame locations change.
 */
public class GrenadeFlamesChanged extends EntityUpdateEvent<Map<String, Vector3D>> {
    public GrenadeFlamesChanged(Map<String, Vector3D> newValue, Map<String, Vector3D> previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
