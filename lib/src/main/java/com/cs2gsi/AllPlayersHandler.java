package com.cs2gsi;

import com.cs2gsi.events.player.AllPlayersUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.PlayerConnected;
import com.cs2gsi.events.player.PlayerDisconnected;
import com.cs2gsi.events.player.PlayerUpdated;
import com.cs2gsi.nodes.Player;

class AllPlayersHandler extends EventHandler<CS2GameEvent> {
    // An observer gets the spectated player twice, in the player block and in allplayers.
    // This is the steam id the player block already reported in the current payload.
    private String reportedByPlayerBlock;
    private boolean broadcastingOwnUpdate;

    AllPlayersHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(PlayerUpdated.class, this::onPlayerUpdated);
        dispatcher.subscribe(AllPlayersUpdated.class, this::onAllPlayersUpdated);
    }

    // The player block is broadcast before allplayers, see GameStateHandler.
    private void onPlayerUpdated(CS2GameEvent e) {
        if (broadcastingOwnUpdate || !(e instanceof PlayerUpdated evt)) {
            return;
        }

        // A different steam id means the spectated player changed. PlayerHandler skips that diff,
        // so the allplayers entry still has to go out.
        boolean samePlayer = evt.newValue.steamId.equals(evt.previousValue.steamId);
        reportedByPlayerBlock = samePlayer ? evt.newValue.steamId : null;
    }

    private void onAllPlayersUpdated(CS2GameEvent e) {
        if (!(e instanceof AllPlayersUpdated evt)) {
            return;
        }

        String alreadyReported = reportedByPlayerBlock;
        reportedByPlayerBlock = null;

        for (var playerEntry : evt.newValue.entrySet()) {
            if (!evt.previousValue.containsKey(playerEntry.getKey())) {
                // Player did not exist before.
                dispatcher.broadcast(new PlayerConnected(playerEntry.getValue()));
                continue;
            }

            Player previousPlayer = evt.previousValue.get(playerEntry.getKey());

            if (!playerEntry.getValue().equals(previousPlayer) && !playerEntry.getKey().equals(alreadyReported)) {
                broadcastingOwnUpdate = true;

                try {
                    dispatcher.broadcast(new PlayerUpdated(playerEntry.getValue(), previousPlayer, playerEntry.getKey()));
                } finally {
                    broadcastingOwnUpdate = false;
                }
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
