package com.cs2gsi;

import com.cs2gsi.events.bomb.BombStateUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.map.MapUpdated;
import com.cs2gsi.events.round.RoundPhaseUpdated;
import com.cs2gsi.events.round.RoundUpdated;
import com.cs2gsi.events.team.TeamRoundLoss;
import com.cs2gsi.events.team.TeamRoundVictory;
import com.cs2gsi.nodes.BombState;
import com.cs2gsi.nodes.Map;
import com.cs2gsi.nodes.Phase;
import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.Round;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundHandlerTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<BombStateUpdated> bombStateEvents = new ArrayList<>();
    private final List<RoundPhaseUpdated> phaseEvents = new ArrayList<>();
    private final List<TeamRoundVictory> victoryEvents = new ArrayList<>();
    private final List<TeamRoundLoss> lossEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new RoundHandler(dispatcher);

        dispatcher.subscribe(BombStateUpdated.class, e -> bombStateEvents.add((BombStateUpdated) e));
        dispatcher.subscribe(RoundPhaseUpdated.class, e -> phaseEvents.add((RoundPhaseUpdated) e));
        dispatcher.subscribe(TeamRoundVictory.class, e -> victoryEvents.add((TeamRoundVictory) e));
        dispatcher.subscribe(TeamRoundLoss.class, e -> lossEvents.add((TeamRoundLoss) e));
    }

    private static Round round(String json) {
        return new Round(JsonParser.parseString(json).getAsJsonObject());
    }

    private static Map map(String json) {
        return new Map(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void bombStateAndPhaseChangesAreBroadcast() {
        Round previous = round("{\"phase\": \"live\", \"bomb\": \"planted\"}");
        Round current = round("{\"phase\": \"over\", \"bomb\": \"exploded\"}");

        dispatcher.broadcast(new RoundUpdated(current, previous));

        assertEquals(1, bombStateEvents.size());
        assertEquals(BombState.Exploded, bombStateEvents.get(0).newValue);
        assertEquals(BombState.Planted, bombStateEvents.get(0).previousValue);

        assertEquals(1, phaseEvents.size());
        assertEquals(Phase.Over, phaseEvents.get(0).newValue);
        assertEquals(Phase.Live, phaseEvents.get(0).previousValue);
    }

    @Test
    void teamVictoryAndDerivedLossUseCachedMapRound() {
        // RoundHandler reads the round number from the most recent MapUpdated event.
        dispatcher.broadcast(new MapUpdated(map("{\"round\": 5}"), new Map()));

        Round previous = round("{\"phase\": \"over\", \"bomb\": \"exploded\"}");
        Round current = round("{\"phase\": \"over\", \"bomb\": \"exploded\", \"win_team\": \"CT\"}");

        dispatcher.broadcast(new RoundUpdated(current, previous));

        assertEquals(1, victoryEvents.size());
        assertEquals(5, victoryEvents.get(0).value);
        assertEquals(PlayerTeam.CT, victoryEvents.get(0).team);

        assertEquals(1, lossEvents.size());
        assertEquals(5, lossEvents.get(0).value);
        assertEquals(PlayerTeam.T, lossEvents.get(0).team);
    }

    @Test
    void unchangedRoundBroadcastsNothing() {
        Round previous = round("{\"phase\": \"live\", \"bomb\": \"planted\"}");
        Round current = round("{\"phase\": \"live\", \"bomb\": \"planted\"}");

        dispatcher.broadcast(new RoundUpdated(current, previous));

        assertTrue(bombStateEvents.isEmpty());
        assertTrue(phaseEvents.isEmpty());
        assertTrue(victoryEvents.isEmpty());
        assertTrue(lossEvents.isEmpty());
    }
}
