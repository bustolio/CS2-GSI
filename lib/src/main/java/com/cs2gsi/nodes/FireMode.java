package com.cs2gsi.nodes;

/**
 * How the primary fire of a weapon reacts to the attack button.
 */
public enum FireMode {
    /** Not a gun, or unknown. */
    Undefined,

    /** Keeps firing while the attack button is held. */
    Automatic,

    /** One shot per click. */
    SemiAutomatic,

    /** One shot per click, then the bolt cycles (AWP, SSG 08). */
    BoltAction,

    /** R8 Revolver: the primary fire needs a held button to cock the hammer. */
    Revolver
}
