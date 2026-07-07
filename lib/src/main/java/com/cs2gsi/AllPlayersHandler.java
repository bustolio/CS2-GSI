package com.cs2gsi;

import com.cs2gsi.events.player.AllPlayersUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.PlayerConnected;
import com.cs2gsi.events.player.PlayerDisconnected;
import com.cs2gsi.events.player.PlayerUpdated;
import com.cs2gsi.nodes.Player;

class AllPlayersHandler extends EventHandler<CS2GameEvent> {
    AllPlayersHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(AllPlayersUpdated.class, this::onAllPlayersUpdated);
    }

    private void onAllPlayersUpdated(CS2GameEvent e) {
        if (!(e instanceof AllPlayersUpdated evt)) {
            return;
        }

        for (var playerEntry : evt.newValue.entrySet()) {
            if (!evt.previousValue.containsKey(playerEntry.getKey())) {
                // Player did not exist before.
                dispatcher.broadcast(new PlayerConnected(playerEntry.getValue()));
                continue;
            }

            Player previousPlayer = evt.previousValue.get(playerEntry.getKey());

            if (!playerEntry.getValue().equals(previousPlayer)) {
                dispatcher.broadcast(new PlayerUpdated(playerEntry.getValue(), previousPlayer, playerEntry.getKey()));
            }
        }

        for (var previousPlayerEntry : evt.previousValue.entrySet()) {
            if (!evt.newValue.containsKey(previousPlayerEntry.getKey())) {
                // Player does not exist anymore.
                dispatcher.broadcast(new PlayerDisconnected(previousPlayerEntry.getValue()));
            }
        }
    }
}
