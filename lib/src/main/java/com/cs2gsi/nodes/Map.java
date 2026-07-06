package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Information about the Map.
 */
public class Map extends Node {
    private static final Pattern ROUND_PATTERN = Pattern.compile("(\\d+)");

    /**
     * The game mode.
     */
    public final GameMode mode;

    /**
     * The map name.
     */
    public final String name;

    /**
     * The current map phase.
     */
    public final Phase phase;

    /**
     * The current round.
     */
    public final int round;

    /**
     * The Counter-Terrorist statistics.
     */
    public final TeamStatistics ctStatistics;

    /**
     * The Terrorist statistics.
     */
    public final TeamStatistics tStatistics;

    /**
     * The number of matches required to win the series.
     */
    public final int numberOfMatchesToWinSeries;

    /**
     * The amount of current spectators.
     */
    public final int currentSpectators;

    /**
     * The total amount of souvenirs.
     */
    public final int souvenirsTotal;

    /**
     * The round conclusions. Key is round number, value is the round conclusion.
     */
    public final LinkedHashMap<Integer, RoundConclusion> roundWins = new LinkedHashMap<>();

    public Map() {
        this(null);
    }

    public Map(JsonObject parsedData) {
        super(parsedData);

        mode = getEnum(GameMode.class, "mode");
        name = getString("name");
        phase = getEnum(Phase.class, "phase");
        round = getInt("round");
        ctStatistics = new TeamStatistics(getJObject("team_ct"));
        tStatistics = new TeamStatistics(getJObject("team_t"));
        numberOfMatchesToWinSeries = getInt("num_matches_to_win_series");
        currentSpectators = getInt("current_spectators");
        souvenirsTotal = getInt("souvenirs_total");

        getMatchingStrings(getJObject("round_wins"), ROUND_PATTERN, (matcher, str) -> {
            int roundNumber = Integer.parseInt(matcher.group(1));
            RoundConclusion roundConclusion = toEnum(RoundConclusion.class, str);

            roundWins.put(roundNumber, roundConclusion);
        });
    }

    @Override
    public String toString() {
        return "["
                + "Mode: " + mode + ", "
                + "Name: " + name + ", "
                + "Phase: " + phase + ", "
                + "Round: " + round + ", "
                + "CTStatistics: " + ctStatistics + ", "
                + "TStatistics: " + tStatistics + ", "
                + "NumberOfMatchesToWinSeries: " + numberOfMatchesToWinSeries + ", "
                + "CurrentSpectators: " + currentSpectators + ", "
                + "SouvenirsTotal: " + souvenirsTotal + ", "
                + "RoundWins: " + roundWins
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Map other
                && mode == other.mode
                && name.equals(other.name)
                && phase == other.phase
                && round == other.round
                && ctStatistics.equals(other.ctStatistics)
                && tStatistics.equals(other.tStatistics)
                && numberOfMatchesToWinSeries == other.numberOfMatchesToWinSeries
                && currentSpectators == other.currentSpectators
                && souvenirsTotal == other.souvenirsTotal
                && roundWins.equals(other.roundWins);
    }

    @Override
    public int hashCode() {
        int hashCode = 897951664;
        hashCode = hashCode * -645891238 + mode.hashCode();
        hashCode = hashCode * -645891238 + name.hashCode();
        hashCode = hashCode * -645891238 + phase.hashCode();
        hashCode = hashCode * -645891238 + Integer.hashCode(round);
        hashCode = hashCode * -645891238 + ctStatistics.hashCode();
        hashCode = hashCode * -645891238 + tStatistics.hashCode();
        hashCode = hashCode * -645891238 + Integer.hashCode(numberOfMatchesToWinSeries);
        hashCode = hashCode * -645891238 + Integer.hashCode(currentSpectators);
        hashCode = hashCode * -645891238 + Integer.hashCode(souvenirsTotal);
        hashCode = hashCode * -645891238 + roundWins.hashCode();
        return hashCode;
    }
}
