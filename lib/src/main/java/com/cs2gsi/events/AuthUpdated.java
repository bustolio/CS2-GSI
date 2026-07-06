package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Auth update.
 */
public class AuthUpdated extends UpdateEvent<Auth> {
    public AuthUpdated(Auth newValue, Auth previousValue) {
        super(newValue, previousValue);
    }
}
