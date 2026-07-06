package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for overall Provider update.
 */
public class ProviderUpdated extends UpdateEvent<Provider> {
    public ProviderUpdated(Provider newValue, Provider previousValue) {
        super(newValue, previousValue);
    }
}
