package com.cs2gsi.events;

import com.cs2gsi.nodes.helpers.Vector3D;

/**
 * Event for the bomb position change.
 */
public class BombPositionChanged extends UpdateEvent<Vector3D> {
    public BombPositionChanged(Vector3D newValue, Vector3D previousValue) {
        super(newValue, previousValue);
    }
}
