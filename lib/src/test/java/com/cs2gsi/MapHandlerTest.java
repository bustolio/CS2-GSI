package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.map.MapUpdated;
import com.cs2gsi.events.round.RoundConcluded;
import com.cs2gsi.events.round.RoundStarted;
import com.cs2gsi.nodes.Map;
import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.RoundConclusion;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapHandlerTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<RoundConcluded> concludedEvents = new ArrayList<>();
    private final List<RoundStarted> startedEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new MapHandler(dispatcher);

        dispatcher.subscribe(RoundConcluded.class, e -> concludedEvents.add((RoundConcluded) e));
        dispatcher.subscribe(RoundStarted.class, e -> startedEvents.add((RoundStarted) e));
    }

    private static Map map(String json) {
        return new Map(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void roundTransitionReportsTheConclusionOfThePreviousRound() {
        // round_wins is one based, entry "5" belongs to round 4.
        Map previous = map("{\"round\": 4}");
        Map current = map("{\"round\": 5, \"round_wins\": {\"5\": \"ct_win_time\"}}");

        dispatcher.broadcast(new MapUpdated(current, previous));

        assertEquals(1, concludedEvents.size());
        assertEquals(4, concludedEvents.get(0).round);
        assertEquals(RoundConclusion.CT_Win_Time, concludedEvents.get(0).roundConclusionReason);
        assertEquals(PlayerTeam.CT, concludedEvents.get(0).winningTeam);

        assertEquals(1, startedEvents.size());
        assertEquals(5, startedEvents.get(0).round);
    }

    @Test
    void halfTimeFlagsBelongToTheRoundEachEventReports() {
        Map previous = map("{\"round\": 11}");
        Map current = map("{\"round\": 12, \"round_wins\": {\"12\": \"t_win_bomb\"}}");

        dispatcher.broadcast(new MapUpdated(current, previous));

        // Round 11 is the last round of the first half, round 12 opens the second half.
        assertTrue(concludedEvents.get(0).isLastRound);
        assertFalse(concludedEvents.get(0).isFirstRound);
        assertTrue(startedEvents.get(0).isFirstRound);
        assertFalse(startedEvents.get(0).isLastRound);
    }

    @Test
    void roundBeforeTheLastIsNotFlaggedAsLast() {
        Map previous = map("{\"round\": 22}");
        Map current = map("{\"round\": 23, \"round_wins\": {\"23\": \"ct_win_defuse\"}}");

        dispatcher.broadcast(new MapUpdated(current, previous));

        assertFalse(concludedEvents.get(0).isLastRound, "round 22 is not the last round");
        assertTrue(startedEvents.get(0).isLastRound, "round 23 is the last of 24 rounds");
    }
}
