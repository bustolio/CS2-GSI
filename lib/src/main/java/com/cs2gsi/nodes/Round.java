package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the current round of this GameState.
 */
public class Round extends Node {
    /**
     * The current round phase.
     */
    public final Phase phase;

    /**
     * The current bomb state.
     */
    public final BombState bombState;

    /**
     * The round winning team.
     */
    public final PlayerTeam winningTeam;

    public Round() {
        this(null);
    }

    public Round(JsonObject parsedData) {
        super(parsedData);

        phase = getEnum(Phase.class, "phase");
        bombState = getEnum(BombState.class, "bomb");
        winningTeam = getEnum(PlayerTeam.class, "win_team");
    }

    @Override
    public String toString() {
        return "["
                + "Phase: " + phase + ", "
                + "BombState: " + bombState + ", "
                + "WinningTeam: " + winningTeam
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Round other
                && phase == other.phase
                && bombState == other.bombState
                && winningTeam == other.winningTeam;
    }

    @Override
    public int hashCode() {
        int hashCode = 659410546;
        hashCode = hashCode * -321047898 + phase.hashCode();
        hashCode = hashCode * -321047898 + bombState.hashCode();
        hashCode = hashCode * -321047898 + winningTeam.hashCode();
        return hashCode;
    }
}
