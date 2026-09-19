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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateListenerIntegrationTest {
    // Port 0 lets the OS pick a free port, getPort() reports it once the listener runs.
    private static final int ANY_PORT = 0;

    private static HttpRequest.Builder requestTo(GameStateListener listener) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + listener.getPort() + "/"));
    }

    private static int send(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private static int post(GameStateListener listener, String body) throws IOException, InterruptedException {
        return send(requestTo(listener).POST(HttpRequest.BodyPublishers.ofString(body)).build());
    }

    private static String sampleWithRoundPhase(String phase) throws IOException {
        JsonObject payload = GameStateParsingTest.loadSampleGameState();
        payload.getAsJsonObject("round").addProperty("phase", phase);
        return payload.toString();
    }

    @Test
    void httpPostFlowsThroughParsingDiffingAndEventDispatch() throws Exception {
        JsonObject fixture = GameStateParsingTest.loadSampleGameState();

        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
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

            assertTrue(listener.start());
            assertNotEquals(0, listener.getPort(), "getPort() must report the bound port");

            assertEquals(200, post(listener, fixture.toString()));

            assertTrue(stateLatch.await(5, TimeUnit.SECONDS), "onNewGameState did not fire");
            assertEquals("de_dust2", receivedState.get().map.name);
            assertTrue(roundStartedLatch.await(5, TimeUnit.SECONDS), "RoundStarted did not fire");
            assertEquals(5, roundStarted.get().round);

            assertEquals(200, post(listener, sampleWithRoundPhase("over")));

            assertTrue(phaseLatch.await(5, TimeUnit.SECONDS), "RoundPhaseUpdated did not fire on the changed payload");
            assertEquals(Phase.Over, phaseUpdated.get().newValue);
            assertEquals(Phase.Live, phaseUpdated.get().previousValue);
        }
    }

    @Test
    void malformedPayloadIsIgnoredAndListenerStaysAlive() throws Exception {
        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            listener.onNewGameState(state -> stateLatch.countDown());

            assertTrue(listener.start());

            assertEquals(200, post(listener, "this is not json"));
            assertFalse(stateLatch.await(500, TimeUnit.MILLISECONDS),
                    "A malformed payload must not produce a game state");

            assertEquals(200, post(listener, GameStateParsingTest.loadSampleGameState().toString()));
            assertTrue(stateLatch.await(5, TimeUnit.SECONDS),
                    "The listener should keep processing valid payloads after a malformed one");
        }
    }

    @Test
    void refusesRequestsTheGameWouldNotSend() throws Exception {
        String payload = GameStateParsingTest.loadSampleGameState().toString();

        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            listener.onNewGameState(state -> stateLatch.countDown());

            assertTrue(listener.start());

            assertEquals(405, send(requestTo(listener).GET().build()));
            assertEquals(403, send(requestTo(listener)
                    .header("Origin", "https://example.com")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build()), "a browser page posting cross-site carries an Origin header");
            assertEquals(413, post(listener, "x".repeat(4 * 1024 * 1024 + 1)));

            assertFalse(stateLatch.await(500, TimeUnit.MILLISECONDS), "refused requests must not reach handlers");
        }
    }

    @Test
    void withATokenSetOnlyGameStatesCarryingItAreAccepted() throws Exception {
        JsonObject withoutToken = GameStateParsingTest.loadSampleGameState();
        withoutToken.remove("auth");

        JsonObject wrongToken = withoutToken.deepCopy();
        wrongToken.add("auth", new JsonObject());
        wrongToken.getAsJsonObject("auth").addProperty("token", "guess");

        JsonObject rightToken = withoutToken.deepCopy();
        rightToken.add("auth", new JsonObject());
        rightToken.getAsJsonObject("auth").addProperty("token", "s3cret");

        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            listener.onNewGameState(state -> stateLatch.countDown());
            listener.setAuthToken("s3cret");

            assertTrue(listener.start());

            assertEquals(401, post(listener, withoutToken.toString()));
            assertEquals(401, post(listener, wrongToken.toString()));
            assertFalse(stateLatch.await(500, TimeUnit.MILLISECONDS), "rejected game states must not reach handlers");

            assertEquals(200, post(listener, rightToken.toString()));
            assertTrue(stateLatch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void throwingHandlerIsReportedAndDoesNotStopTheOthers() throws Exception {
        Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> reported = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> reported.compareAndSet(null, e));

        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            CountDownLatch overLatch = new CountDownLatch(1);

            listener.onNewGameState(state -> {
                throw new IllegalStateException("broken state handler");
            });
            listener.subscribe(RoundPhaseUpdated.class, e -> {
                throw new IllegalStateException("broken event handler");
            });
            listener.subscribe(RoundPhaseUpdated.class, e -> {
                if (e.newValue == Phase.Over && e.previousValue == Phase.Live) {
                    overLatch.countDown();
                }
            });

            assertTrue(listener.start());

            assertEquals(200, post(listener, GameStateParsingTest.loadSampleGameState().toString()));
            assertEquals(200, post(listener, sampleWithRoundPhase("over")));

            // previousValue == Live proves the handler caches stayed in step after the first failure.
            assertTrue(overLatch.await(5, TimeUnit.SECONDS), "the healthy handler must still get the second payload");
            assertInstanceOf(IllegalStateException.class, reported.get());
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(original);
        }
    }

    @Test
    void handlerMayWaitForAnotherThreadThatReadsTheCurrentState() throws Exception {
        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            AtomicReference<String> mapSeenByOtherThread = new AtomicReference<>();
            CountDownLatch done = new CountDownLatch(1);

            // Same shape as SwingUtilities.invokeAndWait(() -> listener.getCurrentGameState()).
            listener.onNewGameState(state -> {
                mapSeenByOtherThread.set(CompletableFuture
                        .supplyAsync(() -> listener.getCurrentGameState().map.name)
                        .orTimeout(5, TimeUnit.SECONDS)
                        .join());
                done.countDown();
            });

            assertTrue(listener.start());
            assertEquals(200, post(listener, GameStateParsingTest.loadSampleGameState().toString()));

            assertTrue(done.await(10, TimeUnit.SECONDS), "the handler deadlocked against getCurrentGameState()");
            assertEquals("de_dust2", mapSeenByOtherThread.get());
        }
    }

    @Test
    void canBeRestartedAfterStop() throws Exception {
        try (GameStateListener listener = new GameStateListener(ANY_PORT)) {
            CountDownLatch stateLatch = new CountDownLatch(1);
            listener.onNewGameState(state -> stateLatch.countDown());

            assertTrue(listener.start());
            assertFalse(listener.start(), "a running listener must refuse a second start");

            listener.stop();
            assertFalse(listener.isRunning());
            listener.stop();

            assertTrue(listener.start());
            assertEquals(200, post(listener, GameStateParsingTest.loadSampleGameState().toString()));
            assertTrue(stateLatch.await(5, TimeUnit.SECONDS), "a restarted listener must deliver game states");
        }
    }
}
