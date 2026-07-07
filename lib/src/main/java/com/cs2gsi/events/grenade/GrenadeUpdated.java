package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for a Grenade update.
 */
public class GrenadeUpdated extends EntityUpdateEvent<Grenade> {
    public GrenadeUpdated(Grenade newValue, Grenade previousValue, String entityId) {
        super(newValue, previousValue, entityId);
    }
}
