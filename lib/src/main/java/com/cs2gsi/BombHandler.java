package com.cs2gsi;

import com.cs2gsi.events.bomb.BombDefused;
import com.cs2gsi.events.bomb.BombDefusing;
import com.cs2gsi.events.bomb.BombDropped;
import com.cs2gsi.events.bomb.BombExploded;
import com.cs2gsi.events.bomb.BombPickedup;
import com.cs2gsi.events.bomb.BombPlanted;
import com.cs2gsi.events.bomb.BombPlanting;
import com.cs2gsi.events.bomb.BombPlayerChanged;
import com.cs2gsi.events.bomb.BombPositionChanged;
import com.cs2gsi.events.bomb.BombStateUpdated;
import com.cs2gsi.events.bomb.BombUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.PlayerUpdated;
import com.cs2gsi.nodes.Player;

import java.util.HashMap;

class BombHandler extends EventHandler<CS2GameEvent> {
    private final HashMap<String, Player> playerCache = new HashMap<>();

    BombHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(PlayerUpdated.class, this::onPlayerUpdated);
        dispatcher.subscribe(BombUpdated.class, this::onBombUpdated);
        dispatcher.subscribe(BombStateUpdated.class, this::onBombStateUpdated);
    }

    private void onPlayerUpdated(CS2GameEvent e) {
        if (!(e instanceof PlayerUpdated evt)) {
            return;
        }

        playerCache.put(evt.playerId, evt.newValue);
    }

    private Player getCachedPlayer(String playerId) {
        return playerCache.getOrDefault(playerId, new Player());
    }

    private void onBombUpdated(CS2GameEvent e) {
        if (!(e instanceof BombUpdated evt)) {
            return;
        }

        if (evt.newValue.state != evt.previousValue.state) {
            dispatcher.broadcast(new BombStateUpdated(evt.newValue.state, evt.previousValue.state));

            switch (evt.newValue.state) {
                case Carried -> dispatcher.broadcast(new BombPickedup(getCachedPlayer(evt.newValue.player)));
                case Planting -> dispatcher.broadcast(new BombPlanting(getCachedPlayer(evt.newValue.player)));
                case Defusing -> dispatcher.broadcast(new BombDefusing(getCachedPlayer(evt.newValue.player)));
                default -> {
                }
            }
        }

        if (!evt.newValue.position.equals(evt.previousValue.position)) {
            dispatcher.broadcast(new BombPositionChanged(evt.newValue.position, evt.previousValue.position));
        }

        if (!evt.newValue.player.equals(evt.previousValue.player)) {
            dispatcher.broadcast(new BombPlayerChanged(
                    getCachedPlayer(evt.newValue.player),
                    getCachedPlayer(evt.previousValue.player)));
        }
    }

    private void onBombStateUpdated(CS2GameEvent e) {
        if (!(e instanceof BombStateUpdated evt)) {
            return;
        }

        switch (evt.newValue) {
            case Dropped -> dispatcher.broadcast(new BombDropped());
            case Planted -> dispatcher.broadcast(new BombPlanted());
            case Defused -> dispatcher.broadcast(new BombDefused());
            case Exploded -> dispatcher.broadcast(new BombExploded());
            default -> {
            }
        }
    }
}
