package com.cs2gsi;

import com.cs2gsi.events.AllGrenadesUpdated;
import com.cs2gsi.events.AllPlayersUpdated;
import com.cs2gsi.events.AuthUpdated;
import com.cs2gsi.events.BombUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.MapUpdated;
import com.cs2gsi.events.PhaseCountdownsUpdated;
import com.cs2gsi.events.PlayerUpdated;
import com.cs2gsi.events.ProviderUpdated;
import com.cs2gsi.events.RoundUpdated;

/**
 * Compares consecutive game states and broadcasts section update events.
 */
class GameStateHandler extends EventHandler<CS2GameEvent> {
    private GameState previousGameState = new GameState();

    GameStateHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);
    }

    public void onNewGameState(GameState gameState) {
        if (!gameState.isValid()) {
            // Invalid game state provided, nothing to do here.
            return;
        }

        if (!previousGameState.isValid() && gameState.getPreviously().isValid()) {
            // If the previous game state cache is invalid, attempt to recover it from the current game state.
            previousGameState = gameState.getPreviously();
        }

        // Broadcast changes for providers.

        if (!previousGameState.auth.equals(gameState.auth)) {
            dispatcher.broadcast(new AuthUpdated(gameState.auth, previousGameState.auth));
        }

        if (!previousGameState.provider.equals(gameState.provider)) {
            dispatcher.broadcast(new ProviderUpdated(gameState.provider, previousGameState.provider));
        }

        if (!previousGameState.map.equals(gameState.map)) {
            dispatcher.broadcast(new MapUpdated(gameState.map, previousGameState.map));
        }

        if (!previousGameState.round.equals(gameState.round)) {
            // Depends on MapUpdated. This broadcast must happen after MapUpdated.
            dispatcher.broadcast(new RoundUpdated(gameState.round, previousGameState.round));
        }

        if (!previousGameState.player.equals(gameState.player)) {
            // Depends on ProviderUpdated. This broadcast must happen after ProviderUpdated.
            dispatcher.broadcast(new PlayerUpdated(gameState.player, previousGameState.player, gameState.player.steamId));
        }

        if (!previousGameState.phaseCountdowns.equals(gameState.phaseCountdowns)) {
            dispatcher.broadcast(new PhaseCountdownsUpdated(gameState.phaseCountdowns, previousGameState.phaseCountdowns));
        }

        if (!previousGameState.allPlayers.equals(gameState.allPlayers)) {
            dispatcher.broadcast(new AllPlayersUpdated(gameState.allPlayers, previousGameState.allPlayers));
        }

        if (!previousGameState.allGrenades.equals(gameState.allGrenades)) {
            dispatcher.broadcast(new AllGrenadesUpdated(gameState.allGrenades, previousGameState.allGrenades));
        }

        if (!previousGameState.bomb.equals(gameState.bomb)) {
            // Depends on PlayerUpdated. This broadcast must happen after PlayerUpdated.
            dispatcher.broadcast(new BombUpdated(gameState.bomb, previousGameState.bomb));
        }

        // Finally update the previous game state cache.
        previousGameState = gameState;
    }
}
