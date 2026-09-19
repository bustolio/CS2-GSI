package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.round.RoundUpdated;
import com.cs2gsi.nodes.Phase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateHandlerTest {
    @Test
    void firstGameStateIsDiffedAgainstItsPreviouslyBlock() throws IOException {
        EventDispatcher<CS2GameEvent> dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        GameStateHandler handler = new GameStateHandler(dispatcher);

        List<RoundUpdated> roundEvents = new ArrayList<>();
        dispatcher.subscribe(RoundUpdated.class, e -> roundEvents.add((RoundUpdated) e));

        // The game lists the old values of everything that changed since its last update.
        JsonObject payload = GameStateParsingTest.loadSampleGameState();
        payload.add("previously", JsonParser.parseString("{\"round\": {\"phase\": \"freezetime\"}}"));

        handler.onNewGameState(new GameState(payload));

        assertEquals(1, roundEvents.size());
        assertEquals(Phase.Live, roundEvents.get(0).newValue.phase);
        assertEquals(Phase.Freezetime, roundEvents.get(0).previousValue.phase, "not Undefined, the listener just started");
    }
}
