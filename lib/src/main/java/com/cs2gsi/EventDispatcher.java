package com.cs2gsi;

import com.cs2gsi.events.BaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Dispatches events to type-based subscribers.
 *
 * @param <T> The base event type.
 */
class EventDispatcher<T extends BaseEvent> {
    private final Object subscriptionsLock = new Object();

    private final Map<Class<?>, Set<Consumer<T>>> subscriptions = new HashMap<>();
    private final Map<Class<?>, Set<UnaryOperator<T>>> preProcessors = new HashMap<>();
    private final List<Consumer<T>> gameEventListeners = new CopyOnWriteArrayList<>();

    private final Class<T> baseEventType;

    EventDispatcher(Class<T> baseEventType) {
        this.baseEventType = baseEventType;
    }

    /**
     * Registers a listener that receives every dispatched event.
     */
    public void onGameEvent(Consumer<T> listener) {
        gameEventListeners.add(listener);
    }

    /**
     * Removes a previously registered catch-all listener.
     */
    public void offGameEvent(Consumer<T> listener) {
        gameEventListeners.remove(listener);
    }

    public void registerPreProcessor(Class<? extends T> eventType, UnaryOperator<T> callback) {
        synchronized (subscriptionsLock) {
            preProcessors.computeIfAbsent(eventType, key -> new LinkedHashSet<>()).add(callback);
        }
    }

    public void unregisterPreProcessor(Class<? extends T> eventType, UnaryOperator<T> callback) {
        synchronized (subscriptionsLock) {
            Set<UnaryOperator<T>> callbacks = preProcessors.get(eventType);

            if (callbacks != null) {
                callbacks.remove(callback);
            }
        }
    }

    public void subscribe(Class<? extends T> eventType, Consumer<T> callback) {
        synchronized (subscriptionsLock) {
            subscriptions.computeIfAbsent(eventType, key -> new LinkedHashSet<>()).add(callback);
        }
    }

    public void unsubscribe(Class<? extends T> eventType, Consumer<T> callback) {
        synchronized (subscriptionsLock) {
            Set<Consumer<T>> callbacks = subscriptions.get(eventType);

            if (callbacks != null) {
                callbacks.remove(callback);
            }
        }
    }

    /**
     * Broadcasts a message to subscribers of its exact type, to subscribers of
     * the base event type, and to catch-all game event listeners.
     */
    public void broadcast(T message) {
        synchronized (subscriptionsLock) {
            T msg = message;
            Class<?> eventType = message.getClass();

            if (subscriptions.containsKey(eventType)) {
                // Run pre-processors first.
                Set<UnaryOperator<T>> processors = preProcessors.get(eventType);

                if (processors != null) {
                    for (UnaryOperator<T> preProcessor : new ArrayList<>(processors)) {
                        msg = preProcessor.apply(msg);

                        if (msg == null) {
                            // The message was handled.
                            return;
                        }
                    }
                }

                for (Consumer<T> callback : new ArrayList<>(subscriptions.get(eventType))) {
                    callback.accept(msg);
                }
            }

            if (!eventType.equals(baseEventType)) {
                Set<Consumer<T>> baseSubscribers = subscriptions.get(baseEventType);

                if (baseSubscribers != null) {
                    for (Consumer<T> callback : new ArrayList<>(baseSubscribers)) {
                        callback.accept(msg);
                    }
                }
            }

            for (Consumer<T> listener : gameEventListeners) {
                listener.accept(msg);
            }
        }
    }
}
