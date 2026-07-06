package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the Phase Countdown.
 */
public class PhaseCountdowns extends Node {
    /**
     * The current phase.
     */
    public final Phase phase;

    /**
     * The amount of time (in seconds) until Phase end.
     */
    public final float phaseEndTime;

    public PhaseCountdowns() {
        this(null);
    }

    public PhaseCountdowns(JsonObject parsedData) {
        super(parsedData);

        phase = getEnum(Phase.class, "phase");
        phaseEndTime = getFloat("phase_ends_in");
    }

    @Override
    public String toString() {
        return "["
                + "Phase: " + phase + ", "
                + "PhaseEndTime: " + phaseEndTime
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof PhaseCountdowns other
                && phase == other.phase
                && phaseEndTime == other.phaseEndTime;
    }

    @Override
    public int hashCode() {
        int hashCode = 487516933;
        hashCode = hashCode * -210468954 + phase.hashCode();
        hashCode = hashCode * -210468954 + Float.hashCode(phaseEndTime);
        return hashCode;
    }
}
