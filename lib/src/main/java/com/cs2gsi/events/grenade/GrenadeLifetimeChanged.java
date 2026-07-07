package com.cs2gsi.events;

/**
 * Event for a specific grenade's lifetime change.
 */
public class GrenadeLifetimeChanged extends EntityUpdateEvent<Float> {
    public GrenadeLifetimeChanged(Float newValue, Float previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
