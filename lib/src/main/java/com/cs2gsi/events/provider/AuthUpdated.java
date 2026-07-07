package com.cs2gsi.events.provider;

import com.cs2gsi.events.UpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Auth update.
 */
public class AuthUpdated extends UpdateEvent<Auth> {
    public AuthUpdated(Auth newValue, Auth previousValue) {
        super(newValue, previousValue);
    }
}
