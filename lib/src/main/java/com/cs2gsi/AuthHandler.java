package com.cs2gsi;

import com.cs2gsi.events.AuthUpdated;
import com.cs2gsi.events.CS2GameEvent;

class AuthHandler extends EventHandler<CS2GameEvent> {
    AuthHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(AuthUpdated.class, this::onAuthUpdated);
    }

    private void onAuthUpdated(CS2GameEvent e) {
        if (!(e instanceof AuthUpdated)) {
            return;
        }
    }
}
