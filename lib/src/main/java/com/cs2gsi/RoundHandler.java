package com.cs2gsi;

import com.cs2gsi.events.bomb.BombStateUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.map.MapUpdated;
import com.cs2gsi.events.round.RoundPhaseUpdated;
import com.cs2gsi.events.round.RoundUpdated;
import com.cs2gsi.events.team.TeamRoundLoss;
import com.cs2gsi.events.team.TeamRoundVictory;
import com.cs2gsi.nodes.Map;
import com.cs2gsi.nodes.PlayerTeam;

class RoundHandler extends EventHandler<CS2GameEvent> {
    private Map map = new Map();

    RoundHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(RoundUpdated.class, this::onRoundUpdated);
        dispatcher.subscribe(MapUpdated.class, this::onMapUpdated);
    }

    private void onMapUpdated(CS2GameEvent e) {
        if (!(e instanceof MapUpdated evt)) {
            return;
        }

        map = evt.newValue;
    }

    private void onRoundUpdated(CS2GameEvent e) {
        if (!(e instanceof RoundUpdated evt)) {
            return;
        }

        // Bomb state needs to be propagated before round is considered over.
        if (evt.newValue.bombState != evt.previousValue.bombState) {
            dispatcher.broadcast(new BombStateUpdated(evt.newValue.bombState, evt.previousValue.bombState));
        }

        if (evt.newValue.phase != evt.previousValue.phase) {
            dispatcher.broadcast(new RoundPhaseUpdated(evt.newValue.phase, evt.previousValue.phase));
        }

        if (evt.newValue.winningTeam != evt.previousValue.winningTeam) {
            int currentRound = map.round;

            dispatcher.broadcast(new TeamRoundVictory(currentRound, evt.newValue.winningTeam));
            if (evt.newValue.winningTeam == PlayerTeam.CT) {
                dispatcher.broadcast(new TeamRoundLoss(currentRound, PlayerTeam.T));
            } else if (evt.newValue.winningTeam == PlayerTeam.T) {
                dispatcher.broadcast(new TeamRoundLoss(currentRound, PlayerTeam.CT));
            }
        }
    }
}
