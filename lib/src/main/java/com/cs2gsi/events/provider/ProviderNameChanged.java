package com.cs2gsi.events.provider;

import com.cs2gsi.events.UpdateEvent;

/**
 * Event for the provider game name change.
 */
public class ProviderNameChanged extends UpdateEvent<String> {
    public ProviderNameChanged(String newValue, String previousValue) {
        super(newValue, previousValue);
    }
}
