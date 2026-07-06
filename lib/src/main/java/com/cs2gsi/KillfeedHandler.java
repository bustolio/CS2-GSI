package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.KillFeed;
import com.cs2gsi.events.PlayerDied;
import com.cs2gsi.events.PlayerGotKill;
import com.cs2gsi.events.RoundChanged;
import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

public class KillfeedHandler extends EventHandler<CS2GameEvent> {
    private Player lastKiller = new Player();
    private Player lastVictim = new Player();
    private Weapon killerWeapon = new Weapon();
    private boolean isHeadshot;

    public KillfeedHandler(EventDispatcher<CS2GameEvent> dispatcher) {
        super(dispatcher);

        dispatcher.subscribe(PlayerDied.class, this::onPlayerDied);
        dispatcher.subscribe(PlayerGotKill.class, this::onPlayerGotKill);
        dispatcher.subscribe(RoundChanged.class, this::onRoundChanged);
    }

    private void onPlayerDied(CS2GameEvent e) {
        if (!(e instanceof PlayerDied evt)) {
            return;
        }

        lastVictim = evt.player;

        resolveKillFeed();
    }

    private void onPlayerGotKill(CS2GameEvent e) {
        if (!(e instanceof PlayerGotKill evt)) {
            return;
        }

        lastKiller = evt.player;
        killerWeapon = evt.weapon;
        isHeadshot = evt.isHeadshot;

        resolveKillFeed();
    }

    private void onRoundChanged(CS2GameEvent e) {
        if (!(e instanceof RoundChanged)) {
            return;
        }

        reset();
    }

    private void resolveKillFeed() {
        if (lastKiller.isValid() && lastVictim.isValid() && killerWeapon.isValid()) {
            if (!lastKiller.steamId.equals(lastVictim.steamId)) {
                dispatcher.broadcast(new KillFeed(lastKiller, lastVictim, isHeadshot, killerWeapon));
            }

            reset();
        }
    }

    private void reset() {
        lastKiller = new Player();
        lastVictim = new Player();
        killerWeapon = new Weapon();
        isHeadshot = false;
    }
}
