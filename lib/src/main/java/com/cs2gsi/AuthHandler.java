package com.cs2gsi;

import com.cs2gsi.events.AuthUpdated;
import com.cs2gsi.events.CS2GameEvent;

public class AuthHandler extends EventHandler<CS2GameEvent> {
    public AuthHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(AuthUpdated.class, this::onAuthUpdated);
    }

    private void onAuthUpdated(CS2GameEvent e) {
        if (!(e instanceof AuthUpdated)) {
            return;
        }
    }
}
