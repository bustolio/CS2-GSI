package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityValueEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for an expired Grenade.
 */
public class ExpiredGrenade extends EntityValueEvent<Grenade> {
    public ExpiredGrenade(Grenade value, String entityId) {
        super(value, entityId);
    }
}
