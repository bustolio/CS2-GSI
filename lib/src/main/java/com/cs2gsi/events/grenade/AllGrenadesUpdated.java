package com.cs2gsi.events.grenade;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for overall All Grenades update.
 */
public class AllGrenadesUpdated extends UpdateEvent<AllGrenades> {
    public AllGrenadesUpdated(AllGrenades newValue, AllGrenades previousValue) {
        super(newValue, previousValue);
    }
}
