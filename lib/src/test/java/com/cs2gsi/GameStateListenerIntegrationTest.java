package com.cs2gsi;

import com.cs2gsi.events.round.RoundPhaseUpdated;
import com.cs2gsi.events.round.RoundStarted;
import com.cs2gsi.nodes.Phase;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateListenerIntegrationTest {
    // GameStateListener reports the constructor port verbatim, so an OS-assigned
    // ephemeral port (0) could not be read back; use fixed high ports instead.
    private static final int PIPELINE_PORT = 43117;
    private static final int MALFORMED_PORT = 43118;

    private static int post(int port, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    @Test
    void httpPostFlowsThroughParsingDiffingAndEventDispatch() throws Exception {
        JsonObject fixture = GameStateParsingTest.loadSampleGameState();

        try (GameStateListener listener = new GameStateListener(PIPELINE_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            AtomicReference<GameState> receivedState = new AtomicReference<>();
            listener.onNewGameState(state -> {
                receivedState.set(state);
                stateLatch.countDown();
            });

            CountDownLatch roundStartedLatch = new CountDownLatch(1);
            AtomicReference<RoundStarted> roundStarted = new AtomicReference<>();
            listener.subscribe(RoundStarted.class, e -> {
                roundStarted.set(e);
                roundStartedLatch.countDown();
            });

            // The first payload also raises phase events (Undefined -> Live), so only
            // count the transition the second payload introduces.
            CountDownLatch phaseLatch = new CountDownLatch(1);
            AtomicReference<RoundPhaseUpdated> phaseUpdated = new AtomicReference<>();
            listener.subscribe(RoundPhaseUpdated.class, e -> {
                if (e.newValue == Phase.Over) {
                    phaseUpdated.set(e);
                    phaseLatch.countDown();
                }
            });

            assertTrue(listener.start(), "Could not bind 127.0.0.1:" + PIPELINE_PORT + " (port in use?)");

            assertEquals(200, post(PIPELINE_PORT, fixture.toString()));

            assertTrue(stateLatch.await(5, TimeUnit.SECONDS), "onNewGameState did not fire");
            assertEquals("de_dust2", receivedState.get().map.name);
            assertTrue(roundStartedLatch.await(5, TimeUnit.SECONDS), "RoundStarted did not fire");
            assertEquals(5, roundStarted.get().round);

            JsonObject secondPayload = fixture.deepCopy();
            secondPayload.getAsJsonObject("round").addProperty("phase", "over");

            assertEquals(200, post(PIPELINE_PORT, secondPayload.toString()));

            assertTrue(phaseLatch.await(5, TimeUnit.SECONDS), "RoundPhaseUpdated did not fire on the changed payload");
            assertEquals(Phase.Over, phaseUpdated.get().newValue);
            assertEquals(Phase.Live, phaseUpdated.get().previousValue);
        }
    }

    @Test
    void malformedPayloadIsIgnoredAndListenerStaysAlive() throws Exception {
        try (GameStateListener listener = new GameStateListener(MALFORMED_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            listener.onNewGameState(state -> stateLatch.countDown());

            assertTrue(listener.start(), "Could not bind 127.0.0.1:" + MALFORMED_PORT + " (port in use?)");

            assertEquals(200, post(MALFORMED_PORT, "this is not json"));
            assertFalse(stateLatch.await(500, TimeUnit.MILLISECONDS),
                    "A malformed payload must not produce a game state");

            assertEquals(200, post(MALFORMED_PORT, GameStateParsingTest.loadSampleGameState().toString()));
            assertTrue(stateLatch.await(5, TimeUnit.SECONDS),
                    "The listener should keep processing valid payloads after a malformed one");
        }
    }
}
