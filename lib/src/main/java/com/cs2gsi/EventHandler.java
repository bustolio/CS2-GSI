package com.cs2gsi;

import com.cs2gsi.events.BaseEvent;

/**
 * Base class for handlers that subscribe to an {@link EventDispatcher}.
 *
 * @param <T> The base event type.
 */
abstract class EventHandler<T extends BaseEvent> {
    protected final EventDispatcher<T> dispatcher;

    protected EventHandler(EventDispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
