package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Map update.
 */
public class MapUpdated extends UpdateEvent<Map> {
    public MapUpdated(Map newValue, Map previousValue) {
        super(newValue, previousValue);
    }
}
