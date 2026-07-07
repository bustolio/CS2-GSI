package com.cs2gsi.events.grenade;

import com.cs2gsi.events.EntityValueEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for a new thrown Grenade.
 */
public class NewGrenade extends EntityValueEvent<Grenade> {
    public NewGrenade(Grenade value, String entityId) {
        super(value, entityId);
    }
}
