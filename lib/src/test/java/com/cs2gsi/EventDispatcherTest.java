package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.map.Gameover;
import com.cs2gsi.events.round.RoundStarted;
import com.cs2gsi.events.map.WarmupStarted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventDispatcherTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
    }

    @Test
    void subscribeDeliversToExactEventType() {
        List<CS2GameEvent> received = new ArrayList<>();
        dispatcher.subscribe(WarmupStarted.class, received::add);

        dispatcher.broadcast(new WarmupStarted());
        dispatcher.broadcast(new Gameover());

        assertEquals(1, received.size());
        assertInstanceOf(WarmupStarted.class, received.get(0));
    }

    @Test
    void unsubscribeStopsDelivery() {
        List<CS2GameEvent> received = new ArrayList<>();
        Consumer<CS2GameEvent> callback = received::add;

        dispatcher.subscribe(WarmupStarted.class, callback);
        dispatcher.broadcast(new WarmupStarted());
        dispatcher.unsubscribe(WarmupStarted.class, callback);
        dispatcher.broadcast(new WarmupStarted());

        assertEquals(1, received.size());
    }

    @Test
    void baseTypeSubscriberReceivesSubtypes() {
        List<CS2GameEvent> received = new ArrayList<>();
        dispatcher.subscribe(CS2GameEvent.class, received::add);

        dispatcher.broadcast(new WarmupStarted());
        dispatcher.broadcast(new Gameover());

        assertEquals(2, received.size());
        assertInstanceOf(WarmupStarted.class, received.get(0));
        assertInstanceOf(Gameover.class, received.get(1));
    }

    @Test
    void preProcessorReturningNullShortCircuitsDelivery() {
        List<CS2GameEvent> received = new ArrayList<>();
        List<CS2GameEvent> caughtAll = new ArrayList<>();

        dispatcher.subscribe(WarmupStarted.class, received::add);
        dispatcher.onGameEvent(caughtAll::add);
        dispatcher.registerPreProcessor(WarmupStarted.class, message -> null);

        dispatcher.broadcast(new WarmupStarted());

        assertTrue(received.isEmpty(), "Subscriber should not be called when a pre-processor handles the event");
        assertTrue(caughtAll.isEmpty(), "Catch-all listeners should not be called when a pre-processor handles the event");
    }

    @Test
    void preProcessorTransformsMessage() {
        List<CS2GameEvent> received = new ArrayList<>();

        dispatcher.subscribe(RoundStarted.class, received::add);
        dispatcher.registerPreProcessor(RoundStarted.class, message -> new RoundStarted(99, false, false));

        dispatcher.broadcast(new RoundStarted(1, true, false));

        assertEquals(1, received.size());
        assertEquals(99, ((RoundStarted) received.get(0)).round);
    }

    @Test
    void unregisterPreProcessorRestoresDelivery() {
        List<CS2GameEvent> received = new ArrayList<>();
        java.util.function.UnaryOperator<CS2GameEvent> swallow = message -> null;

        dispatcher.subscribe(WarmupStarted.class, received::add);
        dispatcher.registerPreProcessor(WarmupStarted.class, swallow);
        dispatcher.broadcast(new WarmupStarted());
        dispatcher.unregisterPreProcessor(WarmupStarted.class, swallow);
        dispatcher.broadcast(new WarmupStarted());

        assertEquals(1, received.size());
    }

    @Test
    void catchAllListenerReceivesEveryBroadcast() {
        List<CS2GameEvent> caughtAll = new ArrayList<>();
        Consumer<CS2GameEvent> listener = caughtAll::add;

        dispatcher.onGameEvent(listener);
        dispatcher.broadcast(new WarmupStarted());
        dispatcher.broadcast(new RoundStarted(1, true, false));

        assertEquals(2, caughtAll.size());

        dispatcher.offGameEvent(listener);
        dispatcher.broadcast(new Gameover());

        assertEquals(2, caughtAll.size());
    }
}
