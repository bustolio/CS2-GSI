package com.cs2gsi.events;

/**
 * Event for a single value update.
 *
 * @param <T> The value type.
 */
public class ValueEvent<T> extends CS2GameEvent {
    /**
     * Value.
     */
    public final T value;

    public ValueEvent(T value) {
        this.value = value;
    }
}
