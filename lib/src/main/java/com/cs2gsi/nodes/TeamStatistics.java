package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Class representing team statistics.
 */
public class TeamStatistics extends Node {
    /**
     * The team score.
     */
    public final int score;

    /**
     * The team name.
     */
    public final String name;

    /**
     * The team flag.
     */
    public final String flag;

    /**
     * The consecutive rounds lost by the team.
     */
    public final int consecutiveRoundLosses;

    /**
     * The number of remaining timeouts.
     */
    public final int remainingTimeouts;

    /**
     * The number of matches won in this series.
     */
    public final int matchesWonThisSeries;

    public TeamStatistics() {
        this(null);
    }

    public TeamStatistics(JsonObject parsedData) {
        super(parsedData);

        score = getInt("score");
        name = getString("name");
        flag = getString("flag");
        consecutiveRoundLosses = getInt("consecutive_round_losses");
        remainingTimeouts = getInt("timeouts_remaining");
        matchesWonThisSeries = getInt("matches_won_this_series");
    }

    @Override
    public String toString() {
        return "["
                + "Score: " + score + ", "
                + "Name: " + name + ", "
                + "Flag: " + flag + ", "
                + "ConsecutiveRoundLosses: " + consecutiveRoundLosses + ", "
                + "RemainingTimeouts: " + remainingTimeouts + ", "
                + "MatchesWonThisSeries: " + matchesWonThisSeries
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof TeamStatistics other
                && score == other.score
                && name.equals(other.name)
                && flag.equals(other.flag)
                && consecutiveRoundLosses == other.consecutiveRoundLosses
                && remainingTimeouts == other.remainingTimeouts
                && matchesWonThisSeries == other.matchesWonThisSeries;
    }

    @Override
    public int hashCode() {
        int hashCode = 354721465;
        hashCode = hashCode * -365789412 + Integer.hashCode(score);
        hashCode = hashCode * -365789412 + name.hashCode();
        hashCode = hashCode * -365789412 + flag.hashCode();
        hashCode = hashCode * -365789412 + Integer.hashCode(consecutiveRoundLosses);
        hashCode = hashCode * -365789412 + Integer.hashCode(remainingTimeouts);
        hashCode = hashCode * -365789412 + Integer.hashCode(matchesWonThisSeries);
        return hashCode;
    }
}
