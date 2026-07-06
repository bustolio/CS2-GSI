package com.cs2gsi.events;

/**
 * Event for value change.
 *
 * @param <T> The value type.
 */
public class UpdateEvent<T> extends CS2GameEvent {
    /**
     * New value.
     */
    public final T newValue;

    /**
     * Previous value.
     */
    public final T previousValue;

    public UpdateEvent(T newValue, T previousValue) {
        this.newValue = newValue;
        this.previousValue = previousValue;
    }
}
