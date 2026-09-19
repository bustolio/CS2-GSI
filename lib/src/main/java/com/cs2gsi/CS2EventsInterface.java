package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Typed subscription interface for Counter-Strike 2 game events.
 * <p>
 * Handlers can subscribe either to every game event via {@link #onGameEvent(Consumer)},
 * or to a specific event type via {@link #subscribe(Class, Consumer)}:
 * <pre>{@code
 * listener.subscribe(BombStateUpdated.class, e -> System.out.println(e.newValue));
 * }</pre>
 * Subscribing to a base event type (for example {@code PlayerHealthChanged}) also
 * receives its subtypes (for example {@code PlayerDied}).
 * <p>
 * A handler that throws does not stop the other handlers. The exception goes to the uncaught
 * exception handler of the dispatching thread, which prints it to {@code System.err} unless the
 * application installed its own with {@link Thread#setDefaultUncaughtExceptionHandler}.
 */
public abstract class CS2EventsInterface {
    private final List<Consumer<CS2GameEvent>> gameEventListeners = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends CS2GameEvent>, List<Consumer<? extends CS2GameEvent>>> listeners =
            new ConcurrentHashMap<>();

    /**
     * Registers a handler that is called for every game event.
     */
    public void onGameEvent(Consumer<CS2GameEvent> handler) {
        gameEventListeners.add(handler);
    }

    /**
     * Removes a previously registered catch-all game event handler.
     */
    public void offGameEvent(Consumer<CS2GameEvent> handler) {
        gameEventListeners.remove(handler);
    }

    /**
     * Registers a handler for a specific game event type (and its subtypes).
     */
    public <E extends CS2GameEvent> void subscribe(Class<E> eventType, Consumer<E> handler) {
        listeners.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>()).add(handler);
    }

    /**
     * Removes a previously registered handler for a specific game event type.
     */
    public <E extends CS2GameEvent> void unsubscribe(Class<E> eventType, Consumer<E> handler) {
        List<Consumer<? extends CS2GameEvent>> handlers = listeners.get(eventType);

        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    protected void onNewGameEvent(CS2GameEvent e) {
        for (Consumer<CS2GameEvent> handler : gameEventListeners) {
            deliver(handler, e);
        }

        for (var entry : listeners.entrySet()) {
            if (entry.getKey().isInstance(e)) {
                for (Consumer<? extends CS2GameEvent> handler : entry.getValue()) {
                    deliver((Consumer<CS2GameEvent>) handler, e);
                }
            }
        }
    }

    /**
     * Calls a handler supplied by the application. An exception it throws goes to the thread's
     * uncaught exception handler, so the remaining handlers still run and the failure stays visible.
     */
    static <T> void deliver(Consumer<T> handler, T value) {
        try {
            handler.accept(value);
        } catch (RuntimeException e) {
            Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
        }
    }
}
