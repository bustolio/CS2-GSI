package com.cs2gsi.nodes;

/**
 * Enum list for round conclusion reason.
 */
public enum RoundConclusion {
    /** Undefined. */
    Undefined,

    /** Terrorists win by elimination. */
    T_Win_Elimination,

    /** Terrorists win by bomb detonation. */
    T_Win_Bomb,

    /** Terrorists win by time. */
    T_Win_Time,

    /** Counter-Terrorists win by elimination. */
    CT_Win_Elimination,

    /** Counter-Terrorists win by bomb defusion. */
    CT_Win_Defuse,

    /** Counter-Terrorists win by rescuing a hostage. */
    CT_Win_Rescue,

    /** Counter-Terrorists win by time. */
    CT_Win_Time
}
