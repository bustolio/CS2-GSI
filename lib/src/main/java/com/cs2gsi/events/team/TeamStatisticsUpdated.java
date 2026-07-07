package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific team's Statistics update.
 */
public class TeamStatisticsUpdated extends TeamUpdateEvent<TeamStatistics> {
    public TeamStatisticsUpdated(TeamStatistics newValue, TeamStatistics previousValue, PlayerTeam team) {
        super(newValue, previousValue, team);
    }
}
