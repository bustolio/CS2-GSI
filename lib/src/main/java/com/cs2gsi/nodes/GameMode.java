package com.cs2gsi.nodes;

/**
 * Enum list for each map mode.<br>
 * The round and bomb times are the defaults from the game's {@code cfg/gamemode_*.cfg} files. A
 * server that sets its own {@code mp_roundtime_defuse} or {@code mp_c4timer} differs, and the
 * payload does not show that.
 */
public enum GameMode {
    // Node.toEnum maps numeric payload values by position, so new constants go at the end.

    /** Undefined. */
    Undefined("", 0, 0),

    /** Custom game mode. */
    Custom("Custom", 0, 0),

    /** War games game mode. */
    Skirmish("Skirmish", 0, 0),

    /** Competitive game mode. */
    Competitive("Competitive", 115, 40),

    /** Wingman game mode. */
    Scrimcomp2v2("Wingman", 90, 40),

    /** Weapons Expert game mode. */
    Scrimcomp5v5("Weapons Expert", 0, 0),

    /** Casual game mode. */
    Casual("Casual", 135, 40),

    /** Mission game mode. */
    Cooperative("Co-op", 0, 0),

    /** Training game mode. */
    Training("Training", 0, 0),

    /** Deathmatch game mode. */
    Deathmatch("Deathmatch", 0, 0);

    /**
     * The name a player knows, e.g. {@code Wingman} for {@code Scrimcomp2v2}. Empty for
     * {@code Undefined}.
     */
    public final String displayName;

    /**
     * The length of a round on a bomb defusal map in seconds ({@code mp_roundtime_defuse}). 0 if
     * the mode has no rounds (Deathmatch) or the game has no fixed value for it. Casual hostage
     * maps run 120 seconds instead of 135.
     */
    public final int roundSeconds;

    /**
     * The time from bomb plant to explosion in seconds ({@code mp_c4timer}). 0 if the mode has no
     * bomb or the game has no fixed value for it.
     */
    public final int bombSeconds;

    GameMode(String displayName, int roundSeconds, int bombSeconds) {
        this.displayName = displayName;
        this.roundSeconds = roundSeconds;
        this.bombSeconds = bombSeconds;
    }
}
