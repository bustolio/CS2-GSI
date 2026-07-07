package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

/**
 * Event for player killfeed.
 */
public class KillFeed extends CS2GameEvent {
    /**
     * The killer.
     */
    public final Player killer;

    /**
     * The victim.
     */
    public final Player victim;

    /**
     * Was the kill a headshot?
     */
    public final boolean isHeadshot;

    /**
     * The weapon used to kill.
     */
    public final Weapon weapon;

    public KillFeed(Player killer, Player victim, boolean isHeadshot, Weapon weapon) {
        this.killer = killer;
        this.victim = victim;
        this.isHeadshot = isHeadshot;
        this.weapon = weapon;
    }
}
