package com.cs2gsi.nodes;

/**
 * Enum list for each bomb activity.
 */
public enum BombState {
    /** Undefined. */
    Undefined,

    /** The bomb is carried. */
    Carried,

    /** The bomb is dropped. */
    Dropped,

    /** The bomb is planted. */
    Planted,

    /** The bomb is being planted. */
    Planting,

    /** The bomb is being defused. */
    Defusing,

    /** The bomb detonated. */
    Exploded,

    /** The bomb has been defused. */
    Defused
}
