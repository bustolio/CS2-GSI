package com.cs2gsi.events;

/**
 * Event for specific entity's single value update.
 *
 * @param <T> The value type.
 */
public class EntityValueEvent<T> extends ValueEvent<T> {
    /**
     * The associated entity ID.
     */
    public final String entityId;

    public EntityValueEvent(T value, String entityId) {
        super(value);
        this.entityId = entityId;
    }
}
