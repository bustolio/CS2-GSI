package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

/**
 * Event for specific player earning a kill.
 */
public class PlayerGotKill extends PlayerEvent {
    /**
     * Was the kill a headshot?
     */
    public final boolean isHeadshot;

    /**
     * The weapon used to earn the kill.
     */
    public final Weapon weapon;

    /**
     * Was the kill an ace kill?
     */
    public final boolean isAce;

    public PlayerGotKill(boolean isHeadshot, Weapon weapon, boolean isAce, Player player) {
        super(player);
        this.isHeadshot = isHeadshot;
        this.weapon = weapon;
        this.isAce = isAce;
    }
}
