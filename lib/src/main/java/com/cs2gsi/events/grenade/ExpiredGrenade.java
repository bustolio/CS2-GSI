package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for an expired Grenade.
 */
public class ExpiredGrenade extends EntityValueEvent<Grenade> {
    public ExpiredGrenade(Grenade value, String entityId) {
        super(value, entityId);
    }
}
