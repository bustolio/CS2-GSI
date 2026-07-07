package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.provider.ProviderNameChanged;
import com.cs2gsi.events.provider.ProviderTimestampChanged;
import com.cs2gsi.events.provider.ProviderUpdated;

class ProviderHandler extends EventHandler<CS2GameEvent> {
    ProviderHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(ProviderUpdated.class, this::onProviderUpdated);
    }

    private void onProviderUpdated(CS2GameEvent e) {
        if (!(e instanceof ProviderUpdated evt)) {
            return;
        }

        if (!evt.newValue.name.equals(evt.previousValue.name)) {
            dispatcher.broadcast(new ProviderNameChanged(evt.newValue.name, evt.previousValue.name));
        }

        if (evt.newValue.timestamp != evt.previousValue.timestamp) {
            dispatcher.broadcast(new ProviderTimestampChanged(evt.newValue.timestamp, evt.previousValue.timestamp));
        }
    }
}
