package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityUpdateEvent;

/**
 * Event for a specific grenade's effect time change.
 */
public class GrenadeEffectTimeChanged extends EntityUpdateEvent<Float> {
    public GrenadeEffectTimeChanged(Float newValue, Float previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
