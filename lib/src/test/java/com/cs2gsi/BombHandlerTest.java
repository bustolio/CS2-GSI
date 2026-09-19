package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.bomb.BombPlanted;
import com.cs2gsi.events.bomb.BombStateUpdated;
import com.cs2gsi.events.bomb.BombUpdated;
import com.cs2gsi.events.round.RoundUpdated;
import com.cs2gsi.nodes.Bomb;
import com.cs2gsi.nodes.Round;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BombHandlerTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<BombStateUpdated> stateEvents = new ArrayList<>();
    private final List<BombPlanted> plantedEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new RoundHandler(dispatcher);
        new BombHandler(dispatcher);

        dispatcher.subscribe(BombStateUpdated.class, e -> stateEvents.add((BombStateUpdated) e));
        dispatcher.subscribe(BombPlanted.class, e -> plantedEvents.add((BombPlanted) e));
    }

    private static Round round(String json) {
        return new Round(JsonParser.parseString(json).getAsJsonObject());
    }

    private static Bomb bomb(String state) {
        return new Bomb(JsonParser.parseString("{\"state\": \"" + state + "\"}").getAsJsonObject());
    }

    @Test
    void plantReportedByRoundAndBombBlockCountsOnce() {
        // A payload with both blocks, in the order GameStateHandler broadcasts them.
        dispatcher.broadcast(new RoundUpdated(round("{\"phase\": \"live\", \"bomb\": \"planted\"}"), round("{\"phase\": \"live\"}")));
        dispatcher.broadcast(new BombUpdated(bomb("planted"), bomb("planting")));

        assertEquals(1, stateEvents.size());
        assertEquals(1, plantedEvents.size());
    }

    @Test
    void plantIsReportedAgainInTheNextRound() {
        dispatcher.broadcast(new RoundUpdated(round("{\"bomb\": \"planted\"}"), round("{}")));
        dispatcher.broadcast(new RoundUpdated(round("{\"bomb\": \"exploded\"}"), round("{\"bomb\": \"planted\"}")));
        dispatcher.broadcast(new RoundUpdated(round("{}"), round("{\"bomb\": \"exploded\"}")));
        dispatcher.broadcast(new RoundUpdated(round("{\"bomb\": \"planted\"}"), round("{}")));

        assertEquals(2, plantedEvents.size());
    }

    @Test
    void abortedDefuseIsNotASecondPlant() {
        dispatcher.broadcast(new BombUpdated(bomb("planted"), bomb("planting")));
        dispatcher.broadcast(new BombUpdated(bomb("defusing"), bomb("planted")));
        dispatcher.broadcast(new BombUpdated(bomb("planted"), bomb("defusing")));

        assertEquals(1, plantedEvents.size());
    }
}
