package com.cs2gsi;

import com.cs2gsi.events.grenade.AllGrenadesUpdated;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.grenade.ExpiredGrenade;
import com.cs2gsi.events.grenade.GrenadeEffectTimeChanged;
import com.cs2gsi.events.grenade.GrenadeFlamesChanged;
import com.cs2gsi.events.grenade.GrenadeLifetimeChanged;
import com.cs2gsi.events.grenade.GrenadePositionChanged;
import com.cs2gsi.events.grenade.GrenadeUpdated;
import com.cs2gsi.events.grenade.GrenadeVelocityChanged;
import com.cs2gsi.events.grenade.NewGrenade;
import com.cs2gsi.nodes.Grenade;

class AllGrenadesHandler extends EventHandler<CS2GameEvent> {
    AllGrenadesHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(AllGrenadesUpdated.class, this::onAllGrenadesUpdated);
    }

    private void onAllGrenadesUpdated(CS2GameEvent e) {
        if (!(e instanceof AllGrenadesUpdated evt)) {
            return;
        }

        for (var grenadeEntry : evt.newValue.entrySet()) {
            if (!evt.previousValue.containsKey(grenadeEntry.getKey())) {
                // Grenade did not exist before.
                dispatcher.broadcast(new NewGrenade(grenadeEntry.getValue(), grenadeEntry.getKey()));
                continue;
            }

            Grenade previousGrenade = evt.previousValue.get(grenadeEntry.getKey());
            Grenade grenade = grenadeEntry.getValue();
            String grenadeId = grenadeEntry.getKey();

            if (!grenade.equals(previousGrenade)) {
                dispatcher.broadcast(new GrenadeUpdated(grenade, previousGrenade, grenadeId));

                if (!grenade.position.equals(previousGrenade.position)) {
                    dispatcher.broadcast(new GrenadePositionChanged(grenade.position, previousGrenade.position, grenadeId));
                }

                if (!grenade.velocity.equals(previousGrenade.velocity)) {
                    dispatcher.broadcast(new GrenadeVelocityChanged(grenade.velocity, previousGrenade.velocity, grenadeId));
                }

                if (grenade.lifetime != previousGrenade.lifetime) {
                    dispatcher.broadcast(new GrenadeLifetimeChanged(grenade.lifetime, previousGrenade.lifetime, grenadeId));
                }

                if (grenade.effectTime != previousGrenade.effectTime) {
                    dispatcher.broadcast(new GrenadeEffectTimeChanged(grenade.effectTime, previousGrenade.effectTime, grenadeId));
                }

                if (!grenade.flames.equals(previousGrenade.flames)) {
                    dispatcher.broadcast(new GrenadeFlamesChanged(grenade.flames, previousGrenade.flames, grenadeId));
                }
            }
        }

        for (var previousGrenadeEntry : evt.previousValue.entrySet()) {
            if (!evt.newValue.containsKey(previousGrenadeEntry.getKey())) {
                // Grenade does not exist anymore.
                dispatcher.broadcast(new ExpiredGrenade(previousGrenadeEntry.getValue(), previousGrenadeEntry.getKey()));
            }
        }
    }
}
