package com.cs2gsi.nodes;

/**
 * Enum list for each phase.
 */
public enum Phase {
    /** Undefined. */
    Undefined,

    /** The game is in freeze time. */
    Freezetime,

    /** The round or game is undergoing. */
    Live,

    /** The game is in warmup. */
    Warmup,

    /** The game is paused. */
    Paused,

    /** The game is paused by Terrorist timeout. */
    Timeout_T,

    /** The game is paused by Counter-Terrorist timeout. */
    Timeout_CT,

    /** The game is over. */
    Gameover,

    /** The game is in intermission. */
    Intermission,

    /** The round is over. */
    Over,

    /** The round is over by bomb detonation. */
    Bomb,

    /** The round is over by bomb defusal. */
    Defuse
}
