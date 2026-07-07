package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for Map phase change.
 */
public class MapPhaseChanged extends UpdateEvent<Phase> {
    public MapPhaseChanged(Phase newValue, Phase previousValue) {
        super(newValue, previousValue);
    }
}
