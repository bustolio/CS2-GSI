package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the Match Statistics.
 */
public class MatchStats extends Node {
    /**
     * The number of kills.
     */
    public final int kills;

    /**
     * The number of assists.
     */
    public final int assists;

    /**
     * The number of deaths.
     */
    public final int deaths;

    /**
     * The number of MVPs.
     */
    public final int mvps;

    /**
     * The amount of score.
     */
    public final int score;

    public MatchStats() {
        this(null);
    }

    public MatchStats(JsonObject parsedData) {
        super(parsedData);

        kills = getInt("kills");
        assists = getInt("assists");
        deaths = getInt("deaths");
        mvps = getInt("mvps");
        score = getInt("score");
    }

    @Override
    public String toString() {
        return "["
                + "Kills: " + kills + ", "
                + "Assists: " + assists + ", "
                + "Deaths: " + deaths + ", "
                + "MVPs: " + mvps + ", "
                + "Score: " + score
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof MatchStats other
                && kills == other.kills
                && assists == other.assists
                && deaths == other.deaths
                && mvps == other.mvps
                && score == other.score;
    }

    @Override
    public int hashCode() {
        int hashCode = 986324056;
        hashCode = hashCode * -987302423 + Integer.hashCode(kills);
        hashCode = hashCode * -987302423 + Integer.hashCode(assists);
        hashCode = hashCode * -987302423 + Integer.hashCode(deaths);
        hashCode = hashCode * -987302423 + Integer.hashCode(mvps);
        hashCode = hashCode * -987302423 + Integer.hashCode(score);
        return hashCode;
    }
}
