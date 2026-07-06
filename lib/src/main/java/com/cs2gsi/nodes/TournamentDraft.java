package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * A class representing tournament draft information.
 */
public class TournamentDraft extends Node {
    /**
     * The game state.
     */
    public final DraftState state;

    /**
     * The ID for this event.
     */
    public final int eventId;

    /**
     * The ID for this event's stage.
     */
    public final int stageId;

    /**
     * The ID of the first team.
     */
    public final int firstTeamId;

    /**
     * The ID of the second team.
     */
    public final int secondTeamId;

    /**
     * The name of the event.
     */
    public final String event;

    /**
     * The name of the stage.
     */
    public final String stage;

    /**
     * The name of the first team.
     */
    public final String firstTeamName;

    /**
     * The name of the second team.
     */
    public final String secondTeamName;

    public TournamentDraft() {
        this(null);
    }

    public TournamentDraft(JsonObject parsedData) {
        super(parsedData);

        state = getEnum(DraftState.class, "state");
        eventId = getInt("eventid");
        stageId = getInt("stageid");
        firstTeamId = getInt("teamid1");
        secondTeamId = getInt("teamid2");
        event = getString("event");
        stage = getString("stage");
        firstTeamName = getString("team1");
        secondTeamName = getString("team2");
    }

    @Override
    public String toString() {
        return "["
                + "State: " + state + ", "
                + "EventID: " + eventId + ", "
                + "StageID: " + stageId + ", "
                + "FirstTeamID: " + firstTeamId + ", "
                + "SecondTeamID: " + secondTeamId + ", "
                + "Event: " + event + ", "
                + "Stage: " + stage + ", "
                + "FirstTeamName: " + firstTeamName + ", "
                + "SecondTeamName: " + secondTeamName
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof TournamentDraft other
                && state == other.state
                && eventId == other.eventId
                && stageId == other.stageId
                && firstTeamId == other.firstTeamId
                && secondTeamId == other.secondTeamId
                && event.equals(other.event)
                && stage.equals(other.stage)
                && firstTeamName.equals(other.firstTeamName)
                && secondTeamName.equals(other.secondTeamName);
    }

    @Override
    public int hashCode() {
        int hashCode = 988971238;
        hashCode = hashCode * -301854564 + state.hashCode();
        hashCode = hashCode * -301854564 + Integer.hashCode(eventId);
        hashCode = hashCode * -301854564 + Integer.hashCode(stageId);
        hashCode = hashCode * -301854564 + Integer.hashCode(firstTeamId);
        hashCode = hashCode * -301854564 + Integer.hashCode(secondTeamId);
        hashCode = hashCode * -301854564 + event.hashCode();
        hashCode = hashCode * -301854564 + stage.hashCode();
        hashCode = hashCode * -301854564 + firstTeamName.hashCode();
        hashCode = hashCode * -301854564 + secondTeamName.hashCode();
        return hashCode;
    }
}
