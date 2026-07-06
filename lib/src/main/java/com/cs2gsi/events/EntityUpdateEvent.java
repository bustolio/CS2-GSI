package com.cs2gsi.events;

/**
 * Event for specific entity's value change.
 *
 * @param <T> The value type.
 */
public class EntityUpdateEvent<T> extends UpdateEvent<T> {
    /**
     * The associated entity ID.
     */
    public final String entityId;

    public EntityUpdateEvent(T newValue, T previousValue, String entityId) {
        super(newValue, previousValue);
        this.entityId = entityId;
    }
}
